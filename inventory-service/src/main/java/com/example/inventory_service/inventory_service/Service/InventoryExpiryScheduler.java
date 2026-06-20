package com.example.inventory_service.inventory_service.Service;

import com.example.inventory_service.inventory_service.Entity.Inventory;
import com.example.inventory_service.inventory_service.Entity.InventoryReservationRequest;
import com.example.inventory_service.inventory_service.Enum.ReservationStatus;
import com.example.inventory_service.inventory_service.Repository.InventoryRepository;
import com.example.inventory_service.inventory_service.Repository.InventoryReservationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryExpiryScheduler {

    private final InventoryReservationRequestRepository reservationRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    @Scheduled(cron = "0 */5 * * * *") // every 5 minutes
    public void expireAndReleaseReservedStock()    {
        LocalDateTime now = LocalDateTime.now();

        // Step 1: Mark expired reservations
        int markedCount = reservationRepository.markExpiredReservations(now);
        log.info("Marked {} reservations as EXPIRED", markedCount);

        // Step 2: Release stock for expired but unreleased reservations
        List<InventoryReservationRequest> toRelease =
                reservationRepository.findExpiredUnreleasedReservations();

        if (toRelease.isEmpty()) {
            log.info("No expired reservations to release");
            return;
        }

        // Group by SKU and sum units to release
        Map<String, Integer> skuToUnitsMap = toRelease.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getInventory().getSku(),
                        Collectors.summingInt(InventoryReservationRequest::getUnitReserved)
                ));

        // Bulk update inventory
        for (Map.Entry<String, Integer> entry : skuToUnitsMap.entrySet()) {
            inventoryRepository.releaseReservedStock(entry.getKey(), entry.getValue());
        }

        // Mark reservations as released
        List<Long> reservationIds = toRelease.stream()
                .map(InventoryReservationRequest::getInventoryReservationId)
                .collect(Collectors.toList());

        reservationRepository.markAsReleased(reservationIds);

        log.info("Released stock for {} SKUs, {} reservations",
                skuToUnitsMap.size(), toRelease.size());
    }
}

