package com.example.rate_limiter.dto;

public class RateLimitRequest {
    private String clientId;
    private String resourceName;

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
}
