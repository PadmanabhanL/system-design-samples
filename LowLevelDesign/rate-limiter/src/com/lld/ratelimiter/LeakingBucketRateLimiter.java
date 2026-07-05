package com.lld.ratelimiter;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of Leaking Bucket Rate Limiting Algorithm.
 * We simulate the bucket processing by calculating the leaked amount based on time passed, 
 * rather than having a background thread actively removing items from a queue.
 */
public class LeakingBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final int leakRate; // Requests per second
    private final Map<String, Bucket> clientBuckets;

    public LeakingBucketRateLimiter(int capacity, int leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.clientBuckets = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean allowRequest(String clientId) {
        clientBuckets.putIfAbsent(clientId, new Bucket(0, Instant.now().toEpochMilli()));
        Bucket bucket = clientBuckets.get(clientId);

        leak(bucket);

        if (bucket.waterLevel < capacity) {
            bucket.waterLevel++;
            return true;
        }

        return false;
    }

    private void leak(Bucket bucket) {
        long now = Instant.now().toEpochMilli();
        long timePassedMillis = now - bucket.lastLeakTimestamp;
        
        // Calculate how much water should have leaked based on leak rate (requests per second)
        // leakRate requests / 1000 ms = X requests / timePassedMillis
        long leakedAmount = (timePassedMillis * leakRate) / 1000;

        if (leakedAmount > 0) {
            bucket.waterLevel = Math.max(0, bucket.waterLevel - (int)leakedAmount);
            bucket.lastLeakTimestamp = now;
        }
    }

    private static class Bucket {
        int waterLevel;
        long lastLeakTimestamp;

        Bucket(int waterLevel, long lastLeakTimestamp) {
            this.waterLevel = waterLevel;
            this.lastLeakTimestamp = lastLeakTimestamp;
        }
    }
}
