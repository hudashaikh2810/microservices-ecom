package com.order_service.order_servie.Exceptions;

public class InventoryNotReservedException extends RuntimeException{
    public InventoryNotReservedException(String message)
    {
        super(message);
    }
}
