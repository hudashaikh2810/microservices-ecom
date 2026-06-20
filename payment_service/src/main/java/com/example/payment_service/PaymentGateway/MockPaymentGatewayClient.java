package com.example.payment_service.PaymentGateway;

import com.example.payment_service.Record.GatewayOrderResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentGatewayClient implements PaymentGatewayClient {
    @Override
    public GatewayOrderResponse createOrder(BigDecimal amount, String currency) {
        return new GatewayOrderResponse(
                "gw_order_" + UUID.randomUUID(),
                amount,
                currency
        );
    }


}
