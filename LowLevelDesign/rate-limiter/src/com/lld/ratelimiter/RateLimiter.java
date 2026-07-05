package com.lld.ratelimiter;

/**
 * Interface defining the rate limiter behavior.
 */
public interface RateLimiter {
    /**
     * Determines whether a given request from a client should be allowed.
     * @param clientId Identifier for the client making the request.
     * @return true if the request is allowed, false if rate limited.
     */
    boolean allowRequest(String clientId);
}
