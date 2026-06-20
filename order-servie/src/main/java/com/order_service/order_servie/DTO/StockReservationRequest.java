package com.order_service.order_servie.DTO;

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

