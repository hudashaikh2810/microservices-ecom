package com.example.product_service.Exception;

import java.util.List;

public class InvalidProductAttributeException extends RuntimeException {
    private final List<String> errors;

    public InvalidProductAttributeException(List<String> errors) {
        super("Invalid product attributes");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
