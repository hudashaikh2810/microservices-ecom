package com.example.rate_limiter.strategy;

import com.example.rate_limiter.dto.RateLimitConfig;
import com.example.rate_limiter.dto.RateLimitResult;

public interface RateLimiterStrategy {
    RateLimitResult checkLimit(String clientId, RateLimitConfig config);
    String getAlgorithmType();
}
