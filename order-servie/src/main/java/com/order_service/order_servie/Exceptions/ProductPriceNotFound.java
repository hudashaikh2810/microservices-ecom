package com.order_service.order_servie.Exceptions;

public class ProductPriceNotFound extends RuntimeException{

    public ProductPriceNotFound(String message)
    {
        super(message);
    }
}
