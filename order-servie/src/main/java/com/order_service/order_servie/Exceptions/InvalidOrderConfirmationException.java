package com.order_service.order_servie.Exceptions;

public class InvalidOrderConfirmationException extends RuntimeException{
    public InvalidOrderConfirmationException(String message)
    {
        super(message);
    }
}
