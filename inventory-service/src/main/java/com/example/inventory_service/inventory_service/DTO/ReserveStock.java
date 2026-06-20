package com.example.inventory_service.inventory_service.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveStock {
    Long orderId;
    List<StockReservationRequest> stockReservationRequest;
}
