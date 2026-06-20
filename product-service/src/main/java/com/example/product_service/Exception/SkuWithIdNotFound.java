package com.example.product_service.Exception;

public class SkuWithIdNotFound extends RuntimeException{
    public SkuWithIdNotFound(String message) {
        super(message);
    }
}