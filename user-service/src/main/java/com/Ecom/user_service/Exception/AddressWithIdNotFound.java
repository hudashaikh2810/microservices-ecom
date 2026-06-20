package com.Ecom.user_service.Exception;

public class AddressWithIdNotFound extends RuntimeException{
    public AddressWithIdNotFound(String message)
    {
        super(message);
    }
}
