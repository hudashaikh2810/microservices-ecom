package com.example.auth_service.Exception;

import java.io.InvalidClassException;

public class InvalidTokenException extends RuntimeException  {
    public InvalidTokenException(String message)
    {
super(message);
    }
}
