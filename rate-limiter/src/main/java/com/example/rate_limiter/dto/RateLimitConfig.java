package com.example.rate_limiter.dto;


public class RateLimitConfig {
    private String algorithm;
    private long capacity;
    private double refillRatePerSecond;

    public RateLimitConfig() {}

    public RateLimitConfig(String algorithm, long capacity, double refillRatePerSecond) {
        this.algorithm = algorithm;
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    public String getAlgorithm() { return algorithm; }
    public long getCapacity() { return capacity; }
    public double getRefillRatePerSecond() { return refillRatePerSecond; }
}
