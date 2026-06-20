package com.example.inventory_service.inventory_service.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationRequest {
    private String sku;
    private Integer quantity;

}

