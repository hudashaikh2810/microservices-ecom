package com.example.inventory_service.inventory_service.Exception;

public class InventoryInconsistencyException extends  RuntimeException{
    public InventoryInconsistencyException(String message)
    {
        super(message);
    }
}
