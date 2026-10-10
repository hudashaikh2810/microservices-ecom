package com.example.product_service.Exception;

public class CategoryNotFoundException extends RuntimeException{
    public CategoryNotFoundException(String message)
    {
        super(message);
    }
}

