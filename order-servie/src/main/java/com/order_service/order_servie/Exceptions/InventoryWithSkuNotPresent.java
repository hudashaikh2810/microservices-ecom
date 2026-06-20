package com.order_service.order_servie.Exceptions;

public class InventoryWithSkuNotPresent extends RuntimeException{
    public InventoryWithSkuNotPresent(String message)
    {
        super(message);
    }
}
