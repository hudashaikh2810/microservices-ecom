package com.example.product_service.Exception;

public class ProductWithIdNotFound extends RuntimeException{
    public ProductWithIdNotFound(String message) {
        super(message);
    }
}
