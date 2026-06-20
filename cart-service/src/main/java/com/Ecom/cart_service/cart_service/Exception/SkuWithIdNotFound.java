package com.Ecom.cart_service.cart_service.Exception;

public class SkuWithIdNotFound extends RuntimeException{
    public SkuWithIdNotFound(String message) {
        super(message);
    }
}
