package com.example.inventory_service.inventory_service.Service;

import com.example.inventory_service.inventory_service.DTO.BulkReservationResponse;
import com.example.inventory_service.inventory_service.DTO.InventoryDto;
import com.example.inventory_service.inventory_service.DTO.ReserveStock;
import com.example.inventory_service.inventory_service.DTO.StockReservationRequest;
import com.example.inventory_service.inventory_service.Entity.Inventory;
import com.example.inventory_service.inventory_service.Entity.InventoryActionLog;
import com.example.inventory_service.inventory_service.Entity.InventoryReservationRequest;
import com.example.inventory_service.inventory_service.Enum.InventoryActionStatus;
import com.example.inventory_service.inventory_service.Enum.InventoryActionType;
import com.example.inventory_service.inventory_service.Enum.ReservationStatus;
import com.example.inventory_service.inventory_service.Exception.*;
import com.example.inventory_service.inventory_service.Repository.InventoryActionLogRepository;
import com.example.inventory_service.inventory_service.Repository.InventoryRepository;
import com.example.inventory_service.inventory_service.Repository.InventoryReservationRequestRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final Logger log = LoggerFactory.getLogger(InventoryService.class);
    @Autowired

    private InventoryRepository inventoryRepository;
    @Autowired
    private RedisTemplate<String, InventoryDto> redisTemplate;
    @Autowired
    private RedisService redisService;

    @Autowired
    private ExpireService expireService;

    @Autowired
    private InventoryReservationRequestRepository inventoryReservationRequestRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private InventoryActionLogRepository actionLogRepository;



    public InventoryDto createInventory(InventoryDto inventoryDto) {
        log.info("Inventory with sku=" + inventoryDto.getSku() + " is being converted to entity");
        Inventory inventoryToBeSaved = convertToEntity(inventoryDto);
        log.info("Inventory with sku=" + inventoryDto.getSku() + " successfully converted to entity");
        inventoryToBeSaved.setLastUpdated(LocalDateTime.now());
        Inventory savedInventory = inventoryRepository.save(inventoryToBeSaved);
        log.info("Inventory with sku=" + inventoryDto.getSku() + " successfully saved to db");
        return convertToDto(savedInventory);
    }

    public InventoryDto getInventoryBySku(String sku) {
        String key = formulateKey(sku);
        log.info("Checking cache for SKU={}", key);

        // 1️⃣ Try Redis first
        InventoryDto cached = redisService.get(key);
        if (cached != null) {
            log.info("✅ Cache hit for SKU={}", key);
            return cached;
        }

        // 2️⃣ Fetch from DB
        log.info("Fetching inventory from DB for SKU={}", key);
        InventoryDto dto = inventoryRepository.findBySku(sku)
                .map(this::convertToDto)
                .orElseThrow(() -> new InventoryWithSkuNotPresent(
                        String.format("Inventory with SKU '%s' doesn't exist", sku)
                ));

        // 3️⃣ Update Redis asynchronously (fire-and-forget)
        redisService.safeSetToRedis(key, dto);

        return dto;
    }

    // Update inventory and optionally refresh cache
    public InventoryDto updateInventory(String sku, InventoryDto inventoryDto) {
        InventoryDto dto = inventoryRepository.findBySku(sku)
                .map(inv -> {
                    inv.setLastUpdated(LocalDateTime.now());
                    inv.setReservedStock(inventoryDto.getReservedStock());
                    inv.setTotalStock(inventoryDto.getTotalStock());
                    return convertToDto(inv);
                })
                .orElseThrow(() -> new InventoryWithSkuNotPresent(
                        String.format("Inventory with SKU '%s' doesn't exist", sku)
                ));

        String key = formulateKey(sku);
        // Fire-and-forget cache update
        redisService.safeSetToRedis(key, dto);

        return dto;
    }

    // Helper for Redis key
    private String formulateKey(String sku) {
        return "inventory:" + sku; // better naming convention
    }


    private InventoryDto convertToDto(Inventory inventory) {
        return InventoryDto.builder().reservedStock(inventory.getReservedStock())
                .totalStock(inventory.getTotalStock()).sku(inventory.getSku()).lastUpdated(inventory.getLastUpdated()).build();
    }

    private Inventory convertToEntity(InventoryDto inventoryDto) {
        return Inventory.builder().reservedStock(inventoryDto.getReservedStock())
                .totalStock(inventoryDto.getTotalStock()).sku(inventoryDto.getSku())
                .build();
    }

    @Transactional
    public BulkReservationResponse reserveStockBulk(ReserveStock reserveStock) {
        List<InventoryReservationRequest> inventoryReservationRequestList=inventoryReservationRequestRepository.findByOrderId(reserveStock.getOrderId());
        if(!inventoryReservationRequestList.isEmpty())
        {
           log.info("Inventory already reserved for this order with orderId={}",reserveStock.getOrderId());
         return  BulkReservationResponse.builder().success(true).message("Inventory already reserved for this order with id="+reserveStock.getOrderId()).build();
        }
        List<String> unavailableItems = new ArrayList<>();
        List<Inventory> inventoriesToUpdate = new ArrayList<>();
        List<StockReservationRequest> items = reserveStock.getStockReservationRequest();
        List<String> skus=items.stream().map(StockReservationRequest::getSku).toList();
        List<Inventory> inventories=inventoryRepository.findBySkusWithLock(skus);
        Map<String, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(Inventory::getSku, Function.identity()));        // Step 1: Check availability for ALL items first
        for (StockReservationRequest item : items) {
           Inventory inventory=inventoryMap.get(item.getSku());
            if (inventory == null) {
                throw new InventoryWithSkuNotPresent("Inventory not found for SKU: " + item.getSku());
            }


            Integer availableStock = inventory.getTotalStock();

            if (availableStock < item.getQuantity()) {
                unavailableItems.add(item.getSku() + " (Available: " + availableStock +
                        ", Requested: " + item.getQuantity() + ")");
            } else {

                inventoriesToUpdate.add(inventory);
            }
        }

        // Step 2: If ANY item is unavailable, rollback transaction
        if (!unavailableItems.isEmpty()) {
            return BulkReservationResponse.failure(
                    "Insufficient stock for items: " + String.join(", ", unavailableItems)
            );
        }

        // Step 3: Reserve stock for ALL items (only if all are available)
        for (int i = 0; i < items.size(); i++) {
            Inventory inventory = inventoriesToUpdate.get(i);
            StockReservationRequest item = items.get(i);


            inventory.setReservedStock(inventory.getReservedStock() + item.getQuantity());
            inventory.setLastUpdated(LocalDateTime.now());
inventory.setTotalStock(inventory.getTotalStock()-item.getQuantity());
            InventoryReservationRequest inventoryReservationRequest = InventoryReservationRequest.builder().inventory(inventory)
                    .reservedAt(LocalDateTime.now())
                    .reservationStatus(ReservationStatus.RESERVED)
                    .reservationExpiry(LocalDateTime.now().plusMinutes(5))
                    .inventory(inventory)
                    .orderId(reserveStock.getOrderId())
                    .unitReserved(item.getQuantity()).build();
            inventory.getInventoryReservationRequest().add(inventoryReservationRequest);

        }

        inventoryRepository.saveAll(inventoriesToUpdate);

        return BulkReservationResponse.success("All items reserved successfully");
    }

    @Transactional
    public void confirmOrderBulk(ReserveStock orderConfirmRequest) {
        // Validate input
        if (orderConfirmRequest == null || orderConfirmRequest.getStockReservationRequest().isEmpty()) {
            throw new IllegalArgumentException("Invalid order confirmation request");
        }

        LocalDateTime now = LocalDateTime.now();

        // Fetch reservations
        List<InventoryReservationRequest> inventoryReservationRequests =
                inventoryReservationRequestRepository.findByOrderId(orderConfirmRequest.getOrderId())
                        .stream()
                        .filter(r -> r.getReservationStatus().equals(ReservationStatus.RESERVED))
                        .collect(Collectors.toList());

        if (inventoryReservationRequests.isEmpty()) {
            throw new ReservationNotFoundException("No active reservations found");
        }

        // Validate count
        if (inventoryReservationRequests.size() != orderConfirmRequest.getStockReservationRequest().size()) {
            expireService.expireAndFreeInBulk(orderConfirmRequest.getOrderId(),LocalDateTime.now());
            throw new InvalidOrderConfirmationException("Reservation count mismatch");
        }

        // Separate expired vs valid reservations FIRST (before any modifications)
        List<InventoryReservationRequest> expiredReservations = new ArrayList<>();
        List<InventoryReservationRequest> validReservations = new ArrayList<>();

        for (InventoryReservationRequest reservation : inventoryReservationRequests) {
            if (reservation.getReservationExpiry().isBefore(now)) {
                expiredReservations.add(reservation);
            } else {
                validReservations.add(reservation);
            }
        }

        // If ANY reservation expired, free stock and abort
        if (!expiredReservations.isEmpty()) {
            // Free stock for ALL expired reservations for this order
            expireService.expireAndFreeInBulk(orderConfirmRequest.getOrderId(), now);

            throw new ReservationExpiredException(
                    String.format("Order has %d expired reservations out of %d total. Stock freed.",
                            expiredReservations.size(),
                            inventoryReservationRequests.size())
            );
        }

        // At this point, ALL reservations are valid - proceed with confirmation

        // Extract SKUs and lock in sorted order (deadlock prevention)
        List<String> skus = validReservations.stream()
                .map(r -> r.getInventory().getSku())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<Inventory> inventories = inventoryRepository.findBySkusWithLock(skus);

        if (inventories.size() != skus.size()) {
            throw new InventoryWithSkuNotPresent("Some SKUs not found");
        }

        // Create maps
        Map<String, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(Inventory::getSku, Function.identity()));

        Map<String, Integer> requestedMap = orderConfirmRequest.getStockReservationRequest().stream()
                .collect(Collectors.toMap(
                        StockReservationRequest::getSku,
                        StockReservationRequest::getQuantity
                ));

        // Process each valid reservation
        for (InventoryReservationRequest reservationRequest : validReservations) {
            String sku = reservationRequest.getInventory().getSku();
            Inventory inventory = inventoryMap.get(sku);

            // Validate against request
            Integer requestedQty = requestedMap.get(sku);
            if (requestedQty == null || !requestedQty.equals(reservationRequest.getUnitReserved())) {
                throw new InvalidOrderConfirmationException("Quantity mismatch for SKU: " + sku);
            }

            // Validate stock consistency
            if (inventory.getReservedStock() < reservationRequest.getUnitReserved()) {
                throw new InventoryInconsistencyException("Insufficient reserved stock for SKU: " + sku);
            }

            // Update inventory - deduct from reserved stock
            inventory.setReservedStock(inventory.getReservedStock() - reservationRequest.getUnitReserved());
            inventory.setLastUpdated(now);
        }

        // Batch update reservations to CONFIRMED status
        int updatedCount = inventoryReservationRequestRepository
                .confirmActiveReservationsForOrder(orderConfirmRequest.getOrderId(), now);

        if (updatedCount != validReservations.size()) {
            throw new ConcurrentModificationException(
                    "Expected to confirm " + validReservations.size() +
                            " reservations but updated " + updatedCount + ". Possible concurrent modification."
            );
        }

        // Batch save inventories
        inventoryRepository.saveAll(inventories);
    }



    @Transactional
    public void releaseReservedStockBulk(ReserveStock orderConfirmRequest) {



           if (orderConfirmRequest == null || orderConfirmRequest.getStockReservationRequest().isEmpty()) {
            throw new IllegalArgumentException("Invalid order confirmation request");
        }

        // Fetch reservations
        List<InventoryReservationRequest> inventoryReservationRequests =
                inventoryReservationRequestRepository.findByOrderId(orderConfirmRequest.getOrderId())
                        .stream()
                        .filter(r -> r.getReservationStatus().equals(ReservationStatus.RESERVED))
                        .filter(r -> !r.isReleased())  // Extra safety
                        .collect(Collectors.toList());
log.info("Inventory reservation size="+inventoryReservationRequests.size());
        if (inventoryReservationRequests.isEmpty()) {
            throw new ReservationNotFoundException("No active reservations found");
        }

        List<Long> reservationIds = inventoryReservationRequests.stream()
                .map(InventoryReservationRequest::getInventoryReservationId)
                .collect(Collectors.toList());

        // Atomic update - only updates if status=RESERVED AND isReleased=false
        int updatedCount = inventoryReservationRequestRepository.updateStatusByIds(
                reservationIds,
                ReservationStatus.RELEASED
        );

        // If no rows updated, someone else already processed these
        if (updatedCount == 0) {
            log.warn("No reservations updated - already processed by another thread");
            return;
        }
        entityManager.flush();
        entityManager.clear();
log.info("Updated count is not zeroooo");
        // Only release stock for successfully updated reservations
        // Re-fetch to get only the ones that were actually updated
        List<InventoryReservationRequest> updatedReservations =
                inventoryReservationRequestRepository.findAllById(reservationIds)
                        .stream()
                        .filter(r -> r.getReservationStatus().equals(ReservationStatus.RELEASED))
                        .collect(Collectors.toList());
log.info("Size="+updatedReservations.size());
        Map<String, Integer> skuToUnitsMap = updatedReservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getInventory().getSku(),
                        Collectors.summingInt(InventoryReservationRequest::getUnitReserved)
                ));
log.info("Map size="+skuToUnitsMap.size());
        skuToUnitsMap.forEach((sku, units) ->
                inventoryRepository.releaseReservedStock(sku, units)
        );

        log.info("Released {} reservations for order {}", updatedCount, orderConfirmRequest.getOrderId());

    }

    @Transactional
    public void restockInventoryForCancelledOrder(String orderId,
                                                  Map<String, Integer> skuQuantity)
    {
        InventoryActionLog actionLog;

        // 1️⃣ Idempotency gate
        try {
            actionLog = actionLogRepository.save(
                    new InventoryActionLog(
                            orderId,
                            InventoryActionType.CANCEL_CONFIRMED_ORDER,
                            InventoryActionStatus.IN_PROGRESS
                    )
            );
        } catch (DataIntegrityViolationException ex) {
            //
            throw new OrderAlreadyProcessed("Order with id="+orderId+" is already processed");
        }

        // 2️⃣ Lock inventory rows
        List<Inventory> inventoryList =
                inventoryRepository.findBySkusWithLock(
                        new ArrayList<>(skuQuantity.keySet())
                );

        // 3️⃣ Update stock
        for (Inventory inventory : inventoryList) {
            inventory.setTotalStock(
                    inventory.getTotalStock()
                            + skuQuantity.get(inventory.getSku())
            );
        }

        inventoryRepository.saveAll(inventoryList);

        // 4️⃣ Mark SUCCESS using SAME entity
        actionLog.setStatus(InventoryActionStatus.SUCCESS);
        // no explicit save required; JPA dirty checking will update it

    }


}
