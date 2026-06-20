package com.order_service.order_servie.Exceptions;

public class SkuWithIdNotFound extends RuntimeException{
    public SkuWithIdNotFound(String message) {
        super(message);
    }
}