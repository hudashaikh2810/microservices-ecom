package com.order_service.order_servie.DTO;

import com.order_service.order_servie.Enums.OrderStatus;
import com.order_service.order_servie.Enums.PaymentStatus;
import com.order_service.order_servie.Enums.PaymentType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Long id;

    private Long userId;

    private String orderTrackingId;

    private Double amountToPay;

    private PaymentType paymentType;

    private PaymentStatus paymentStatus;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private OrderAddressDto deliveryAddress;

    private List<OrderItemDto> orderItems;
}

