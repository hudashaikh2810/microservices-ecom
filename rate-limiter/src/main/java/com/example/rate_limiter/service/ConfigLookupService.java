package com.example.rate_limiter.service;

import com.example.rate_limiter.dto.RateLimitConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;

@Service
public class ConfigLookupService {


    public final static HashMap<String,RateLimitConfig> rateLimitConfig = new HashMap<>();

    private static final long CONFIG_TTL_SECONDS = 3600; // ~1 hour, per your design

    public ConfigLookupService() {

    }

    public RateLimitConfig getConfig(String resourceName) {
        String cacheKey = "rateLimiter:config:" + resourceName;


        if (rateLimitConfig.containsKey(cacheKey)) {
            return rateLimitConfig.get(cacheKey);
        }

        // Cache miss — fetch from Config Server (stubbed for now)
        RateLimitConfig fetched = fetchFromConfigServer(resourceName);
        rateLimitConfig.put(cacheKey,fetched);
        return fetched;
    }

    private RateLimitConfig fetchFromConfigServer(String resourceName) {
        return new RateLimitConfig("TOKEN_BUCKET",3,0.05);
    }


}
