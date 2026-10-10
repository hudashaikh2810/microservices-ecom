package com.example.product_service.Exception;

public class InvalidImageStateException extends RuntimeException {
    public InvalidImageStateException(String message) {
        super(message);
    }
}
