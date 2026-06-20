package com.example.inventory_service.inventory_service.Exception;

public class InventoryWithSkuNotPresent extends RuntimeException{
    public InventoryWithSkuNotPresent(String message)
    {
        super(message);
    }
}
