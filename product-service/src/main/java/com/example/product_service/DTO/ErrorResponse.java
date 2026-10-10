package com.example.product_service.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private List<String> details;   // null/empty when there's just one message
    private Instant timestamp;

    public ErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
        this.timestamp = Instant.now();
    }
}
