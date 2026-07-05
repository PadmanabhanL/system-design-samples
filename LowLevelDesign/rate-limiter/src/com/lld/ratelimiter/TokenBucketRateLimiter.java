package com.lld.ratelimiter;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of Token Bucket Rate Limiting Algorithm.
 */
public class TokenBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final int refillRate; // Tokens per second
    private final Map<String, Bucket> clientBuckets;

    public TokenBucketRateLimiter(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.clientBuckets = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized boolean allowRequest(String clientId) {
        clientBuckets.putIfAbsent(clientId, new Bucket(capacity, Instant.now().getEpochSecond()));
        Bucket bucket = clientBuckets.get(clientId);

        refill(bucket);

        if (bucket.tokens > 0) {
            bucket.tokens--;
            return true;
        }

        return false;
    }

    private void refill(Bucket bucket) {
        long now = Instant.now().getEpochSecond();
        long timePassed = now - bucket.lastRefillTimestamp;
        int tokensToAdd = (int) (timePassed * refillRate);

        if (tokensToAdd > 0) {
            bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
            bucket.lastRefillTimestamp = now;
        }
    }

    private static class Bucket {
        int tokens;
        long lastRefillTimestamp;

        Bucket(int tokens, long lastRefillTimestamp) {
            this.tokens = tokens;
            this.lastRefillTimestamp = lastRefillTimestamp;
        }
    }
}
