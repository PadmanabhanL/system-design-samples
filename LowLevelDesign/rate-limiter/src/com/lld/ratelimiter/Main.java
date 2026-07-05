package com.lld.ratelimiter;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Testing Token Bucket Rate Limiter ---");
        // Capacity 5, refill rate 2 tokens per second
        RateLimiter tokenBucket = new TokenBucketRateLimiter(5, 2);
        
        for (int i = 0; i < 7; i++) {
            System.out.println("Request " + (i + 1) + " allowed: " + tokenBucket.allowRequest("client1"));
        }
        
        System.out.println("Waiting 2 seconds...");
        Thread.sleep(2000); // Should refill ~4 tokens
        
        for (int i = 0; i < 5; i++) {
            System.out.println("Request " + (i + 8) + " allowed: " + tokenBucket.allowRequest("client1"));
        }

        System.out.println("\n--- Testing Fixed Window Rate Limiter ---");
        // Limit 3 requests per 1000ms (1 second)
        RateLimiter fixedWindow = new FixedWindowRateLimiter(3, 1000);
        
        for (int i = 0; i < 5; i++) {
            System.out.println("Request " + (i + 1) + " allowed: " + fixedWindow.allowRequest("client2"));
        }
        
        System.out.println("Waiting 1.1 seconds...");
        Thread.sleep(1100); // Move to next window
        
        for (int i = 0; i < 2; i++) {
            System.out.println("Request " + (i + 6) + " allowed: " + fixedWindow.allowRequest("client2"));
        }
        
        System.out.println("\n--- Testing Sliding Window Log Rate Limiter ---");
        // Limit 3 requests per 1000ms (1 second)
        RateLimiter slidingWindowLog = new SlidingWindowLogRateLimiter(3, 1000);
        
        for (int i = 0; i < 5; i++) {
            System.out.println("Request " + (i + 1) + " allowed: " + slidingWindowLog.allowRequest("client3"));
        }
        
        System.out.println("Waiting 1.1 seconds...");
        Thread.sleep(1100); // Move window past previous requests
        
        for (int i = 0; i < 2; i++) {
            System.out.println("Request " + (i + 6) + " allowed: " + slidingWindowLog.allowRequest("client3"));
        }

        System.out.println("\n--- Testing Leaking Bucket Rate Limiter ---");
        // Capacity 3, leak rate 2 requests per second
        RateLimiter leakingBucket = new LeakingBucketRateLimiter(3, 2);
        
        for (int i = 0; i < 5; i++) {
            System.out.println("Request " + (i + 1) + " allowed: " + leakingBucket.allowRequest("client4"));
        }
        
        System.out.println("Waiting 1.1 seconds...");
        Thread.sleep(1100); // Should leak ~2 requests
        
        for (int i = 0; i < 3; i++) {
            System.out.println("Request " + (i + 6) + " allowed: " + leakingBucket.allowRequest("client4"));
        }
    }
}
