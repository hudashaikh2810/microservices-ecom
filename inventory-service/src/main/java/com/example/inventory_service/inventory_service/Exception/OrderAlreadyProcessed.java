package com.example.inventory_service.inventory_service.Exception;

public class OrderAlreadyProcessed extends RuntimeException{
    public OrderAlreadyProcessed(String message)
    {
        super(message);
    }
}
