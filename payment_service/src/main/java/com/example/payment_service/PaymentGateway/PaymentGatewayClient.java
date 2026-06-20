package com.example.payment_service.PaymentGateway;

import com.example.payment_service.Record.GatewayOrderResponse;

import java.math.BigDecimal;

public interface PaymentGatewayClient {
    GatewayOrderResponse createOrder(BigDecimal amount, String currency);
}
