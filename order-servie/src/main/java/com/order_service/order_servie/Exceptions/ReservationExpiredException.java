package com.order_service.order_servie.Exceptions;

public class ReservationExpiredException extends RuntimeException{
    public ReservationExpiredException(String message)
    {
        super(message);
    }
}
