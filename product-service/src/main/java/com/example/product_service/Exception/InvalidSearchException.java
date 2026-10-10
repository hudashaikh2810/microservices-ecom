package com.example.product_service.Exception;

public class InvalidSearchException extends RuntimeException{
  public  InvalidSearchException(String message)
    {
        super(message);
    }
}
