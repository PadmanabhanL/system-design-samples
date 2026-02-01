package com.paddy.leetcode;

// Implement this SlidingWindowRateLimiter class
// Question: Implement the SlidingWindowRateLimiter class so that it enforces a maximum number of requests within a rolling time window. The allow method is called before each request and should return whether that request is permitted based on the number of requests made in the last window_seconds seconds.
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.*;
class SlidingWindowRateLimiter {

    private final int maxRequests;

    private final double windowSeconds;
    private long startTime;
    private long targetTime;
    private int numberOfRequestsServed;

    public SlidingWindowRateLimiter(int maxRequests, double windowSeconds) {
        // TODO: Implement constructor
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
        this.startTime = System.currentTimeMillis();
        this.numberOfRequestsServed = 0;
        this.targetTime = startTime + ((long) windowSeconds * 1000L);
    }

    public boolean allow() {
        // TODO: Implement allow method

        System.out.println("Number Of Requests So Far: " + numberOfRequestsServed);

        synchronized (SlidingWindowRateLimiter.class) {
            ++numberOfRequestsServed;
        }

        long currentTimeMillis = System.currentTimeMillis();

        System.out.println("Current Time And TargetTime: " + currentTimeMillis + " " + targetTime);

        boolean timestampBreach = currentTimeMillis > targetTime;
        if (timestampBreach) {
            startTime = targetTime;
            targetTime = targetTime + ((long) windowSeconds * 1000);
            synchronized (SlidingWindowRateLimiter.class) {
                numberOfRequestsServed = 0;
            }
        }

        System.out.println("Number Of Requests Served: " + numberOfRequestsServed);
        System.out.println("Time Window:" + (targetTime - startTime));

        boolean requestsBreach = numberOfRequestsServed > maxRequests;
        System.out.println("RequestsBreached? " + requestsBreach);
        System.out.println("TimestampBreached?" + timestampBreach);
        boolean isAllowed = !requestsBreach || timestampBreach;
        return isAllowed;
    }
}

// TEST CODE

class Solution {
    public static void main(String[] args) {
        var SWRL = new SlidingWindowRateLimiter(5, 1);
        try {
            for (int i = 0; i < 5; i++) {
                if (SWRL.allow() != true){
                    System.out.println("Incorrect result for First loop iteration #" + i);
                    return;
                } // should be allowed
                Thread.sleep(100);
            }
            if (SWRL.allow() != false){
                System.out.println("Incorrect result for 6th request");
                return;
            } // should NOT be allowed

            Thread.sleep(500);

            for (int i = 0; i < 5; i++) {
                if (SWRL.allow() != true){
                    System.out.println("Incorrect result [False] for Second loop iteration #" + i);
                    return;
                }; // should be allowed
                if (SWRL.allow() != false){
                    System.out.println("Incorrect result [True] for Second loop iteration #" + i);
                    return;
                }; // should NOT be allowed
                Thread.sleep(100);
            }
            System.out.println("All tests pass");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;
    }
}