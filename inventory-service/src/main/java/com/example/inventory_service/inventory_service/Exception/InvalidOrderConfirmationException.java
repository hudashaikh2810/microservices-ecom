package com.example.inventory_service.inventory_service.Exception;

public class InvalidOrderConfirmationException extends RuntimeException{
    public InvalidOrderConfirmationException(String message)
    {
        super(message);
    }
}
