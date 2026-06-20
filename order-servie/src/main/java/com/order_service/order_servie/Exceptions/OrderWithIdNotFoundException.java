package com.order_service.order_servie.Exceptions;

public class OrderWithIdNotFoundException extends RuntimeException{
    public OrderWithIdNotFoundException(String message)
    {
        super(message);
    }
}
