package com.example.inventory_service.inventory_service.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkReservationResponse {
    private boolean success;
    private String message;
    private List<String> failedItems; // Optional: details of failed items

    public static BulkReservationResponse success(String message) {
        BulkReservationResponse response = new BulkReservationResponse();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    public static BulkReservationResponse failure(String message) {
        BulkReservationResponse response = new BulkReservationResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    // Getters, setters
}
