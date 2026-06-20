package com.example.auth_service.Exception;

public class TokenExpired extends RuntimeException {
    public TokenExpired(String message) {
        super(message);
    }
}
