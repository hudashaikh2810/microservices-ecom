package com.example.inventory_service.inventory_service.Exception;

public class ReservationExpiredException extends RuntimeException{
    public ReservationExpiredException(String message)
    {
        super(message);
    }
}
