package com.example.rate_limiter.controller;

import com.example.rate_limiter.dto.RateLimitRequest;
import com.example.rate_limiter.dto.RateLimitResult;
import com.example.rate_limiter.service.RateLimiterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rateLimit")
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    public RateLimiterController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/check")
    public ResponseEntity<RateLimitResult> check(@RequestBody RateLimitRequest request) {
        RateLimitResult result = rateLimiterService.identifyAlgorithm(
                request.getClientId(), request.getResourceName());

        if (result.isAllowed()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(result);
        }
    }
}
