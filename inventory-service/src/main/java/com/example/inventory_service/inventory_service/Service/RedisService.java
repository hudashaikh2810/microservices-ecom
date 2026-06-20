package com.example.inventory_service.inventory_service.Service;


import com.example.inventory_service.inventory_service.DTO.InventoryDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {
    private final Logger log= LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate<String, InventoryDto> redisTemplate;

    @CircuitBreaker(name = "rediscache", fallbackMethod = "getValueFromRedisFailed")
    @Retry(name = "redisretry", fallbackMethod = "getValueFromRedisFailed")
    public InventoryDto get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public InventoryDto getValueFromRedisFailed(String key, Throwable t) {
        log.warn("⚠️ Redis unavailable while fetching SKU: {}. Skipping cache.", key);
        return null;
    }
    // ✅ Resilience4j-protected Redis set
    @CircuitBreaker(name = "rediscache", fallbackMethod = "setValueFromRedisFailed")
    @Retry(name = "redissetretry", fallbackMethod = "setValueFromRedisFailed")
    public void safeSetToRedis(String key, InventoryDto dto) {
        redisTemplate.opsForValue().set(key, dto, Duration.ofMinutes(10));
    }

    // Fallback for void set method
    public void setValueFromRedisFailed(String key, InventoryDto dto, Throwable t) {
        log.warn("⚠️ Redis unavailable while setting SKU: {}. Skipping cache.", key);
    }
}

