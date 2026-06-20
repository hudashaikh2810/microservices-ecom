package com.order_service.order_servie.Exceptions;

public class OrderCouldNotBeConfirmed extends RuntimeException{
    public OrderCouldNotBeConfirmed(String message)
    {
        super(message);
    }
}
