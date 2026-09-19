package com.example.rate_limiter;


import com.example.rate_limiter.dto.RateLimitConfig;
import com.example.rate_limiter.dto.RateLimitResult;
import com.example.rate_limiter.strategy.TokenBucketStrategy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TokenBucketStrategyConcurrencyTest {

    @Autowired
    private TokenBucketStrategy tokenBucketStrategy;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String CLIENT_ID = "concurrency-test-user";
    private static final long CAPACITY = 10;

    @AfterEach
    void cleanup() {
        // Ensure each test run starts fresh, independent of previous runs' TTL
        stringRedisTemplate.delete("rateLimiter:state:tokenbucket:" + CLIENT_ID);
    }

    @Test
    void concurrentRequests_neverExceedCapacity() throws InterruptedException {
        // Refill rate set to effectively zero for the test's duration, so we're
        // purely testing "does concurrent access ever allow more than `capacity`
        // requests through" — not accidentally letting refill add extra tokens
        // mid-test and muddying the result.
        RateLimitConfig config = new RateLimitConfig("TOKEN_BUCKET", CAPACITY, 0.0000001);

        int numberOfThreads = 50; // deliberately more than capacity
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch readyLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger allowedCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await(); // all threads fire as close to simultaneously as possible
                    RateLimitResult result = tokenBucketStrategy.checkLimit(CLIENT_ID, config);
                    if (result.isAllowed()) {
                        allowedCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();      // wait until every thread is queued up and ready
        startLatch.countDown();  // release them all at once
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // The core assertion: no matter how many threads raced simultaneously,
        // the number ALLOWED must never exceed capacity. If the naive
        // GET-then-SET implementation were still in place, this would
        // intermittently fail with allowedCount > 10.
        assertEquals(CAPACITY, allowedCount.get(),
                "Expected exactly " + CAPACITY + " requests to be allowed under concurrent load, got " + allowedCount.get());
    }
}
