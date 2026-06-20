package com.example.payment_service.Record;

import java.math.BigDecimal;

public record GatewayOrderResponse(
        String gatewayOrderId,
        BigDecimal amount,
        String currency
) {}
