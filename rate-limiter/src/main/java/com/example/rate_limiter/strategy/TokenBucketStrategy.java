package com.example.rate_limiter.strategy;

import com.example.rate_limiter.dto.RateLimitConfig;
import com.example.rate_limiter.dto.RateLimitResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TokenBucketStrategy implements RateLimiterStrategy {

   private final StringRedisTemplate stringRedisTemplate;
   private final RedisScript<List> tokenBucketScript;

    public TokenBucketStrategy(RedisTemplate<String, Object> redisTemplate,StringRedisTemplate stringRedisTemplate) {

       this.stringRedisTemplate = stringRedisTemplate;
        this.tokenBucketScript = RedisScript.of(
                new ClassPathResource("scripts/token_bucket.lua"), List.class);
    }


    @Override
    @SuppressWarnings("unchecked")
    public RateLimitResult checkLimit(String clientId, RateLimitConfig config) {
        String key = "rateLimiter:state:tokenbucket:" + clientId;
        long now = System.currentTimeMillis();

        List<String> keys = Collections.singletonList(key);

        List<Object> result = stringRedisTemplate.execute(
                tokenBucketScript,
                keys,
                String.valueOf(config.getCapacity()),
                String.valueOf(config.getRefillRatePerSecond()),
                String.valueOf(now)
        );

        boolean allowed = Long.parseLong(result.get(0).toString()) == 1L;
        long retryAfter = Long.parseLong(result.get(1).toString());

        return new RateLimitResult(allowed, retryAfter);
    }



    @Override
    public String getAlgorithmType() {
        return "TOKEN_BUCKET";
    }
}