package com.example.product_service.Service;

import com.example.product_service.DTO.ProductMetaDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class RedisService {
    @Autowired
    private RedisTemplate<String, ProductMetaDto> redisTemplate;

    @Autowired
    private RedisTemplate<String, List<Long>> redisTemplateList;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))         // time-to-live
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()
                ));
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }

    public String getHashKeyForPage(String category, int page, int size) {
        return "category:" + category.toLowerCase() + "page:" + page;
    }

    @CircuitBreaker(name = "readFromRedisList", fallbackMethod = "readFromRedisListFailed")
    @Retry(name = "retryReadFromRedisList", fallbackMethod = "readFromRedisListFailed")
    public List<Long> getProductListFromRedis(String key) {
        return redisTemplateList.opsForValue().get(key);
    }

    public List<Long> readFromRedisListFailed(String key, Throwable t) {
        log.info("Read from redis list for key" + key + "failed");
        return null;
    }

    public String getKeyForProduct(Long id) {
        return "id:" + id;
    }

    @CircuitBreaker(name = "readFromRedisMetaData", fallbackMethod = "readFromRedisMetaFailed")
    @Retry(name = "retryReadFromRedisMeta", fallbackMethod = "readFromRedisMetaFailed")
    public ProductMetaDto getProductMetaDto(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public ProductMetaDto readFromRedisMetaFailed(String key, Throwable t) {
        log.info("Read from redis for product meta data with key=" + key + " failed");
        return null;
    }

    @CircuitBreaker(name = "putToRedisList", fallbackMethod = "putToRedisListFailed")
    @Retry(name = "retryPutToRedisList", fallbackMethod = "putToRedisListFailed")
    public void putToRedis(String key, List<Long> productIdList) {
        redisTemplateList.opsForValue().set(key, productIdList);
    }

    public void putToRedisListFailed(String key, List<Long> productIdList, Throwable t) {
        log.info("Cannot put into redis for key=" + key);
    }
    @CircuitBreaker(name = "putToRedis", fallbackMethod = "putToRedisMetaFailed")
    @Retry(name = "retryPutToRedis", fallbackMethod = "putToRedisMetaFailed")
    public void putToRedis(String key, ProductMetaDto metaDto) {
        redisTemplate.opsForValue().set(key, metaDto);
    }
    public void putToRedisMetaFailed(String key, ProductMetaDto metaDto,Throwable t)
    {
        log.info("{Cannot put into redis for key=" + key);
    }
    @CircuitBreaker(name = "deleteFromRedisList", fallbackMethod = "deleteFromRedisFailed")
    @Retry(name = "retryDeleteFromRedis", fallbackMethod = "deleteFromRedisFailed")
    public void deleteFromRedisList(String key) {
        redisTemplateList.delete(key);
    }
    public void deleteFromRedisFailed(String key,Throwable t) {
        log.info("Cannot delete key="+key+" from redis");
    }

    @CircuitBreaker(name = "deleteFromRedisMeta", fallbackMethod = "deleteFromRedisMetaFailed")
    @Retry(name = "retryDeleteFromRedisMeta", fallbackMethod = "deleteFromRedisMetaFailed")
    public void deleteFromRedis(String key) {
        redisTemplateList.delete(key);
    }
    public void deleteFromRedisMetaFailed(String key,Throwable t) {
        log.info("Cannot delete key="+key+" from redis");
    }

}
