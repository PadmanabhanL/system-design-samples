package com.lld.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of Fixed Window Counter Rate Limiting Algorithm.
 */
public class FixedWindowRateLimiter implements RateLimiter {

    private final int limit;
    private final long windowSizeInMillis;
    private final Map<String, Window> clientWindows;

    public FixedWindowRateLimiter(int limit, long windowSizeInMillis) {
        this.limit = limit;
        this.windowSizeInMillis = windowSizeInMillis;
        this.clientWindows = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String clientId) {
        long currentTimeMillis = System.currentTimeMillis();
        long currentWindowKey = currentTimeMillis / windowSizeInMillis;

        clientWindows.putIfAbsent(clientId, new Window(currentWindowKey));
        Window window = clientWindows.get(clientId);

        // Check if we moved to a new window
        if (window.windowKey != currentWindowKey) {
            synchronized (window) {
                if (window.windowKey != currentWindowKey) {
                    window.windowKey = currentWindowKey;
                    window.counter.set(0);
                }
            }
        }

        return window.counter.incrementAndGet() <= limit;
    }

    private static class Window {
        long windowKey;
        AtomicInteger counter;

        Window(long windowKey) {
            this.windowKey = windowKey;
            this.counter = new AtomicInteger(0);
        }
    }
}
