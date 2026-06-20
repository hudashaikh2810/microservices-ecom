package com.order_service.order_servie.Exceptions;

public class OrderCouldNotBeCreated extends RuntimeException {
    public OrderCouldNotBeCreated(String message) {
        super(message);
    }
}
