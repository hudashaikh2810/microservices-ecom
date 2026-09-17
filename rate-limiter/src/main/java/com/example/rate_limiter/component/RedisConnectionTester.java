package com.example.rate_limiter.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisConnectionTester implements CommandLineRunner {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisConnectionTester(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) {
        redisTemplate.opsForValue().set("test:connection", "hello-redis");
        Object value = redisTemplate.opsForValue().get("test:connection");
        System.out.println("Redis test read: " + value);
    }
}
