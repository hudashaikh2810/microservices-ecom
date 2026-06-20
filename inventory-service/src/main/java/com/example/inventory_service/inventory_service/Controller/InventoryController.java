package com.example.inventory_service.inventory_service.Controller;


import com.example.inventory_service.inventory_service.DTO.BulkReservationResponse;
import com.example.inventory_service.inventory_service.DTO.InventoryDto;
import com.example.inventory_service.inventory_service.DTO.ReserveStock;
import com.example.inventory_service.inventory_service.DTO.StockReservationRequest;
import com.example.inventory_service.inventory_service.Exception.InventoryWithSkuNotPresent;
import com.example.inventory_service.inventory_service.Service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    /**
     * Create a new inventory entry
     */
    @PostMapping
    public ResponseEntity<InventoryDto> createInventory(@RequestBody InventoryDto inventoryDto) {
        log.info("🎯 [POST] Create inventory request received | SKU: {}", inventoryDto.getSku());

        InventoryDto created = inventoryService.createInventory(inventoryDto);

        log.info("✅ Inventory created successfully | SKU: {}", created.getSku());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Fetch inventory by SKU
     */
    @GetMapping("/{sku}")
    public ResponseEntity<InventoryDto> getInventoryBySku(@PathVariable String sku) {
        log.info("📦 [GET] Fetch inventory request received | SKU: {}", sku);

        InventoryDto inventory = inventoryService.getInventoryBySku(sku);

        log.info("✅ Inventory fetched successfully | SKU: {}", sku);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Update existing inventory
     */
    @PutMapping("/{sku}")
    public ResponseEntity<InventoryDto> updateInventory(
            @PathVariable String sku,
            @RequestBody InventoryDto inventoryDto) {

        log.info("🛠️ [PUT] Update inventory request received | SKU: {}", sku);

        InventoryDto updated = inventoryService.updateInventory(sku, inventoryDto);

        log.info("✅ Inventory updated successfully | SKU: {}", sku);
        return ResponseEntity.ok(updated);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.debug("🔍 [GET] Health check invoked for Inventory Service");
        return ResponseEntity.ok("✅ Inventory Service is up and running");
    }

    /**
     * Reserve stock for multiple items in bulk
     * POST /api/inventory/reserve/bulk
     */
    @PostMapping("/reserve/bulk")
    public ResponseEntity<BulkReservationResponse> reserveStockBulk(
            @RequestBody ReserveStock items) {

        if (items == null) {
            return ResponseEntity.badRequest()
                    .body(BulkReservationResponse.failure("Request body cannot be empty"));
        }

        BulkReservationResponse response = inventoryService.reserveStockBulk(items);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    /**
     * Confirm order and deduct stock for multiple items in bulk
     * POST /api/inventory/confirm/bulk
     */
    @PostMapping("/confirm/bulk")
    public ResponseEntity<String> confirmOrderBulk(
            @RequestBody ReserveStock items) {



        inventoryService.confirmOrderBulk(items);
        return ResponseEntity.ok("Order confirmed and stock deducted successfully for all items");

    }

    /**
     * Release reserved stock for multiple items in bulk
     * POST /api/inventory/release/bulk
     */
    @PostMapping("/release/bulk")
    public ResponseEntity<String> releaseReservedStockBulk(
            @RequestBody ReserveStock items) {

            inventoryService.releaseReservedStockBulk(items);
            return ResponseEntity.ok("Reserved stock released successfully for all items");
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<String> cancelOrder(@RequestBody HashMap<String,Integer> skuQuantity,@PathVariable String orderId)
    {
        inventoryService.restockInventoryForCancelledOrder(orderId,skuQuantity);
        return ResponseEntity.ok("Inventory added back");
    }
}

