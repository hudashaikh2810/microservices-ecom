package com.example.inventory_service.inventory_service.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDto {
    private String sku;
    private Integer totalStock;
    private Integer reservedStock;
    private LocalDateTime lastUpdated;

}
