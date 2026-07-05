package com.lld.ratelimiter;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of Sliding Window Log Rate Limiting Algorithm.
 */
public class SlidingWindowLogRateLimiter implements RateLimiter {

    private final int limit;
    private final long windowSizeInMillis;
    private final Map<String, Queue<Long>> clientLogs;

    public SlidingWindowLogRateLimiter(int limit, long windowSizeInMillis) {
        this.limit = limit;
        this.windowSizeInMillis = windowSizeInMillis;
        this.clientLogs = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String clientId) {
        long currentTimeMillis = System.currentTimeMillis();
        
        clientLogs.putIfAbsent(clientId, new LinkedList<>());
        Queue<Long> log = clientLogs.get(clientId);

        synchronized (log) {
            // Remove timestamps older than the window
            long windowStart = currentTimeMillis - windowSizeInMillis;
            while (!log.isEmpty() && log.peek() <= windowStart) {
                log.poll();
            }

            // Check if the current log size is within the limit
            if (log.size() < limit) {
                log.offer(currentTimeMillis);
                return true;
            }
            return false;
        }
    }
}
