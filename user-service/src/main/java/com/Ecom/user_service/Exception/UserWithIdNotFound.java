package com.Ecom.user_service.Exception;

public class UserWithIdNotFound extends RuntimeException{
    public UserWithIdNotFound(String message)
    {
        super(message);
    }
}
