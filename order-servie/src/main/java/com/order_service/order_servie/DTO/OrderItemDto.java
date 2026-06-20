package com.order_service.order_servie.DTO;

import com.order_service.order_servie.Enums.OrderType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {

    private Long id;

    private String productId;

    private Integer quantity;

    private Double pricePerUnit;

    private Double totalItemPrice;

    private OrderType orderType;

    private Long orderId;
}

