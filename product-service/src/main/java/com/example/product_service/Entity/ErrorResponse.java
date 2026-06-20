package com.example.product_service.Entity;

import java.time.Instant;

public class ErrorResponse {
    private int statusCode;
    private Instant timestamp;
    private String message;
    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode, Instant timestamp, String message, String path) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.path = path;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
