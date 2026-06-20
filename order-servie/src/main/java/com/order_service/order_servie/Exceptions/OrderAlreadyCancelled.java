package com.order_service.order_servie.Exceptions;

public class OrderAlreadyCancelled extends RuntimeException{
    public OrderAlreadyCancelled(String message)
    {
        super(message);
    }
}
