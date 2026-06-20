package com.order_service.order_servie.Exceptions;

public class OrderCouldNotBeReleased extends RuntimeException{
    public OrderCouldNotBeReleased(String message)
    {
        super(message);
    }
}
