package com.order_service.order_servie.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order_service.order_servie.DTO.ErrorResponse;
import com.order_service.order_servie.Exceptions.*;
import feign.Response;
import feign.codec.ErrorDecoder;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ConcurrentModificationException;

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
                if(errorResponse!=null&&errorResponse.getMessage().contains("No Reservations Found"))
                {
                    return new ReservationNotFoundException(errorResponse.getMessage());
                }
                if(errorResponse!=null && errorResponse.getMessage().contains("SKUs not found"))
                {
                    return new SkuWithIdNotFound(errorResponse.getMessage());
                }
                String message = errorResponse != null ?
                        errorResponse.getMessage() : "The resource couldnot be found";
                // Extract product ID from method key or error message
                return new SkuWithIdNotFound(message);

            case 400:
                return new IllegalArgumentException(
                        errorResponse != null ? errorResponse.getMessage() : "Bad request."
                );

            case 500:
                return new RuntimeException(
                        errorResponse != null ? errorResponse.getMessage() : "Internal server error.Service is down"
                );
            case 409:
                if(errorResponse !=null)
                {
                    message=errorResponse.getMessage();
                    if(message.contains("Reservation count mismatch")||message.contains("Quantity mismatch"))
                    {
                        return new InvalidOrderConfirmationException(errorResponse.getMessage());
                    }
                    if(message.contains("Insufficient reserved stock"))
                    {
                        return new InventoryInconsistencyException(errorResponse.getMessage());
                    }
                    if(message.contains("expired Reservation"))
                    {
                        return new ReservationExpiredException(errorResponse.getMessage());
                    }
                    if(message.contains("Possible concurrent modification."))
                    {
                        return new ConcurrentModificationException(errorResponse.getMessage());
                    }
                    if(message.contains("already processed"))
                    {
                        return new OrderAlreadyCancelled(errorResponse.getMessage());
                    }

                }


            default:
                return new Exception("Generic error: " + response.status());
        }
    }


}
