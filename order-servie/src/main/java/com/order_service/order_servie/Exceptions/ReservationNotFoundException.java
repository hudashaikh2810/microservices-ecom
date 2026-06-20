package com.order_service.order_servie.Exceptions;

public class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message)
    {
        super(message);
    }
}
