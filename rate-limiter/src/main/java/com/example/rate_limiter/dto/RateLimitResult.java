package com.example.rate_limiter.dto;

public class RateLimitResult {
    private final boolean allowed;
    private final long retryAfter;

    public RateLimitResult(boolean allowed, long retryAfter) {
        this.allowed = allowed;
        this.retryAfter = retryAfter;
    }

    public boolean isAllowed() { return allowed; }
    public long getRetryAfter() { return retryAfter; }
}
