package com.Ecom.cart_service.cart_service.config.config;

import com.Ecom.cart_service.cart_service.DTO.ErrorResponse;
import com.Ecom.cart_service.cart_service.Exception.SkuWithIdNotFound;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {

        ErrorResponse errorResponse = null;

        try {
            // Parse error response from product service
            if (response.body() != null) {
                String body = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
                errorResponse = objectMapper.readValue(body, ErrorResponse.class);
            }
        } catch (IOException e) {
            // If parsing fails, use default message
        }

        // Convert HTTP status to appropriate exception
        switch (response.status()) {
            case 404:
                System.out.println("I got error");
                String message = errorResponse != null ?
                        errorResponse.getMessage() : "Sku with Id not found";
                // Extract product ID from method key or error message
                return new SkuWithIdNotFound(message);

            case 400:
                return new IllegalArgumentException(
                        errorResponse != null ? errorResponse.getMessage() : "Bad request"
                );

            case 500:
                return new RuntimeException(
                        errorResponse != null ? errorResponse.getMessage() : "Internal server error"
                );

            default:
                return new Exception("Generic error: " + response.status());
        }
    }


}
