package com.order_service.order_servie.Exceptions;

public class InventoryInconsistencyException extends  RuntimeException{
    public InventoryInconsistencyException(String message)
    {
        super(message);
    }
}
