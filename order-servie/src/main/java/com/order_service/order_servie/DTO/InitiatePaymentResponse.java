package com.order_service.order_servie.DTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiatePaymentResponse {

    private Long paymentId;
    private String gatewayOrderId;
    private BigDecimal amount;
    private String currency;

    // getters & setters
}
