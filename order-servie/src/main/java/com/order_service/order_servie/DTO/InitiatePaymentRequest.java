package com.order_service.order_servie.DTO;

import com.order_service.order_servie.Enums.PaymentType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiatePaymentRequest {

    private Long orderId;
    private BigDecimal amount;
    private String currency;
    private PaymentType paymentType;

    // getters & setters
}

