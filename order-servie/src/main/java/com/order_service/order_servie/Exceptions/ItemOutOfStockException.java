package com.order_service.order_servie.Exceptions;

public class ItemOutOfStockException extends RuntimeException{
    public ItemOutOfStockException(String message)
    {
        super(message);
    }
}

