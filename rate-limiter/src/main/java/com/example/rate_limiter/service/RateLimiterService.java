package com.example.rate_limiter.service;

import com.example.rate_limiter.dto.RateLimitConfig;
import com.example.rate_limiter.dto.RateLimitResult;
import com.example.rate_limiter.strategy.RateLimiterStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RateLimiterService {

    private final Map<String, RateLimiterStrategy> strategies;
    private final ConfigLookupService configLookupService;

    public RateLimiterService(List<RateLimiterStrategy> strategyList,
                              ConfigLookupService configLookupService) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(RateLimiterStrategy::getAlgorithmType, s -> s));
        this.configLookupService = configLookupService;
    }

    public RateLimitResult identifyAlgorithm(String clientId, String resourceName) {
        RateLimitConfig config = configLookupService.getConfig(resourceName);
        RateLimiterStrategy strategy = strategies.get(config.getAlgorithm());

        if (strategy == null) {
            throw new IllegalStateException("No strategy registered for algorithm: " + config.getAlgorithm());
        }

        return strategy.checkLimit(clientId, config);
    }
}
