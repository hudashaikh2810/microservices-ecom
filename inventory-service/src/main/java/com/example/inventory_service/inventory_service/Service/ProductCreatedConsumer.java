package com.example.inventory_service.inventory_service.Service;

import com.example.inventory_service.inventory_service.DTO.InventoryDto;
import com.example.inventory_service.inventory_service.DTO.ProductCreatedEvent;
import com.example.inventory_service.inventory_service.Entity.Inventory;
import com.example.inventory_service.inventory_service.Repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ProductCreatedConsumer {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryRepository inventoryRepository;
    @KafkaListener(
            topics = "sku-created",
            groupId = "inventory-service"
    )
    public void onProductCreated(ProductCreatedEvent event, Acknowledgment ack) {

        log.info("📩 Received ProductCreatedEvent for SKU: {}", event.getSKU());

        // build default inventory
        InventoryDto defaultInventory = InventoryDto.builder()
                .sku(event.getSKU())
                .reservedStock(0)
                .totalStock(0)
                .lastUpdated(LocalDateTime.now())
                .build();

        // create inventory only if it doesn't exist
        if (inventoryRepository.findBySku(event.getSKU()).isEmpty()) {
            log.info("🆕 Creating inventory row for SKU: {}", event.getSKU());
            inventoryService.createInventory(defaultInventory);
        } else {
            log.info("ℹ️ Inventory already exists for SKU: {} (skipping)", event.getSKU());
        }

        // ✅ ACK only after successful processing
        ack.acknowledge();
        log.info("✅ Acknowledged Kafka message for SKU: {}", event.getSKU());
    }
}
