package com.example.payment_service.DTO;

import com.example.payment_service.Enums.PaymentType;
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

