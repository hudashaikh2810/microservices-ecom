package com.Ecom.cart_service.cart_service.Service;

import com.Ecom.cart_service.cart_service.DTO.CartDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class RedisService {
    @Autowired
    private RedisTemplate<String, CartDto> redisTemplate;

    @CircuitBreaker(name = "redisCache", fallbackMethod = "writeToCacheFailedGuestId")
    @Retry(name = "redisRetry", fallbackMethod = "writeToCacheFailedGuestId")
    public void writeToCacheGuestId(Long guestId, CartDto cart) {
        log.info("Going to generate key for user{}", guestId);
        String key = getGuestKey(guestId);
        log.info("Going to update cache");

        redisTemplate.opsForValue().set(key, cart, Duration.ofHours(2));
        log.info("Cache updated successfully");
    }

    public void writeToCacheFailedGuestId(Long guestId, CartDto cart, Throwable t) {
        log.info("Write to cache failed for cart guestIdId={} , skipping redis", guestId);
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "writeToCacheFailed")
    @Retry(name = "redisRetry", fallbackMethod = "writeToCacheFailed")
    public void writeToCache(Long userId, CartDto cart) {
        log.info("Going to generate key for user{}", userId);
        String key = getKey(userId);
        log.info("Going to update cache");

        redisTemplate.opsForValue().set(key, cart, Duration.ofHours(2));
        log.info("Cache updated successfully");
    }

    public void writeToCacheFailed(Long userId, CartDto cart, Throwable t) {
        log.info("Write to cache failed for cart userId={} , skipping redis", cart.getUserId());
    }

    public String getKey(Long userId) {
        return "user:" + userId;
    }

    public String getGuestKey(Long guestId) {
        return "guest:" + guestId;
    }

    @CircuitBreaker(name = "redisCacheRead", fallbackMethod = "readFromCacheFailed")
    @Retry(name = "redReadCache", fallbackMethod = "readFromCacheFailed")
    public CartDto readFromCache(Long userId) {
        log.info("Inside the readFrom cache method for user{}", userId);
        log.info("Going to get the key for user{}", userId);
        String key = getKey(userId);
        CartDto result = redisTemplate.opsForValue().get(key);
        if (result != null) {
            return result;
        }
        return null;
    }

    public CartDto readFromCacheFailed(Long userId, Throwable t) {
        log.warn("Read to cache failed for user{},skipping redis", userId);
        return null;
    }

    @CircuitBreaker(name = "redisCacheRead", fallbackMethod = "readFromCacheFailedByGuestId")
    @Retry(name = "readCacheFail", fallbackMethod = "readFromCacheFailedByGuestId")
    public CartDto readFromCacheByGuestId(Long guestId) {
        log.info("Inside the readFrom cache method for guestId{}", guestId);
        log.info("Going to get the key for guestId{}", guestId);
        String key = getGuestKey(guestId);
        CartDto result = redisTemplate.opsForValue().get(key);
        if (result != null) {
            return result;
        }
        return null;
    }

    public CartDto readFromCacheFailedByGuestId(Long guestId, Throwable t) {
        log.warn("Read to cache failed for user{},skipping redis", guestId);
        return null;
    }

    @CircuitBreaker(name = "deleteFromCache", fallbackMethod = "deleteFromCacheFailed")
    @Retry(name = "deleteFromCache", fallbackMethod = "deleteFromCacheFailed")
    public void removeFromRedisGuestId(Long guestId) {
        String key = getGuestKey(guestId);
        if (redisTemplate.opsForValue().get(key) != null) {
            redisTemplate.delete(key);
        }
    }

    public void deleteFromCacheFailed(Long guestId, Throwable t) {
        log.info("Unable to connect to redis cannot delete the guestId");
    }
}
