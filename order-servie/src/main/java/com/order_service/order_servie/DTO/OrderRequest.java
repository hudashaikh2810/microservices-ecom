package com.order_service.order_servie.DTO;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequest {
  List<StockReservationRequest> stockReservationRequestList;
    private Long userId;
    private OrderAddressDto orderAddressDto;
}
