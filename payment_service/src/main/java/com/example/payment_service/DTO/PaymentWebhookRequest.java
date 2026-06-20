package com.example.payment_service.DTO;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentWebhookRequest {

    private String event;              // payment.success / payment.failed
    private String gatewayPaymentId;   // pay_xxx
    private String gatewayOrderId;     // order_xxx
    private String webhookEventId;     // unique event id from gateway
    private BigDecimal amount;
    private String failureReason;

    // getters & setters
}

