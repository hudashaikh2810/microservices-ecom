package com.example.inventory_service.inventory_service.Exception;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message)
    {
        super(message);
    }
}
