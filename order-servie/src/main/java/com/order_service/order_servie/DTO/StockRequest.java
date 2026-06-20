package com.order_service.order_servie.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockRequest {
    private String orderId;
    private List<StockReservationRequest> stockReservationRequestList;
}
