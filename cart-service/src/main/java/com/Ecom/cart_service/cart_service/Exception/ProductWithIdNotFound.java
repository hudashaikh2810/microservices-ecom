package com.Ecom.cart_service.cart_service.Exception;

public class ProductWithIdNotFound extends RuntimeException{
    public ProductWithIdNotFound(String message) {
        super(message);
    }
}
