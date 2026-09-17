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

    public TokenBucketStrategy(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;


        this.tokenBucketScript = RedisScript.of(
                new ClassPathResource("scripts/token_bucket.lua"), List.class);
    }
    /*
    @Override
    public RateLimitResult checkLimit(String clientId, RateLimitConfig config) {
        String key = "rateLimiter:state:tokenbucket:" + clientId;
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();

        Map<Object, Object> stored = hashOps.entries(key);

        long currentTokens;
        long lastAccessed;

        if (stored.isEmpty()) {
            currentTokens = config.getCapacity();
            lastAccessed = System.currentTimeMillis();
        } else {
            currentTokens = Long.parseLong(stored.get("tokens").toString());
            lastAccessed = Long.parseLong(stored.get("lastAccessed").toString());
        }

        long now = System.currentTimeMillis();
        double elapsedSeconds = (now - lastAccessed) / 1000.0;
        double tokensToAdd = elapsedSeconds * config.getRefillRatePerSecond();

        long tokensAfterRefill = (long) Math.min(config.getCapacity(), currentTokens + tokensToAdd);

        boolean allowed;
        long remainingTokens;
        long retryAfter;

        if (tokensAfterRefill >= 1) {
            allowed = true;
            remainingTokens = tokensAfterRefill - 1;
            retryAfter = 0;
        } else {
            allowed = false;
            remainingTokens = tokensAfterRefill;
            retryAfter = (long) ((1 - tokensAfterRefill) / config.getRefillRatePerSecond());
        }

        Map<String, Object> updated = new HashMap<>();
        updated.put("tokens", remainingTokens);
        updated.put("lastAccessed", now);
        hashOps.putAll(key, updated);

        long ttlSeconds = (long) (config.getCapacity() / config.getRefillRatePerSecond());
        redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));

        return new RateLimitResult(allowed, retryAfter);
    }
*/

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