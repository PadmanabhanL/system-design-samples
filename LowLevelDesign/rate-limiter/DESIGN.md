# Rate Limiter - Low Level Design

## Overview
A Rate Limiter is used to control the rate of traffic sent by a client or a service. It limits the number of requests a user can send to an API within a time window, preventing resource exhaustion and abuse.

## Requirements

### Functional Requirements
- **allowRequest(request):** Returns `true` if the request is allowed, `false` otherwise.
- **Configuration:** Should support defining rules (e.g., 100 requests per minute per user).
- **Multiple Algorithms:** Support various rate-limiting algorithms.

### Non-Functional Requirements
- **Low Latency:** The decision to allow or deny should be extremely fast.
- **Thread-safe:** Safe for concurrent requests from the same user.
- **Scalability:** Able to scale to handle thousands of users.

## Rate Limiting Algorithms/Strategies

### 1. Token Bucket
- **Concept:** A bucket holds tokens, and tokens are added at a fixed rate. Each request consumes a token. If the bucket is empty, the request is dropped.
- **Pros:** Allows a burst of traffic up to the bucket capacity. Memory efficient.
- **Implementation:** Needs to store the last refill timestamp and the current token count per user.

### 2. Leaking Bucket
- **Concept:** Requests enter a queue (bucket). The queue is processed at a constant fixed rate. If the queue is full, incoming requests are dropped.
- **Pros:** Smoothes out bursty traffic, providing a stable outflow rate.
- **Implementation:** Queue based.

### 3. Fixed Window Counter
- **Concept:** Time is divided into fixed windows (e.g., 12:00-12:01, 12:01-12:02). A counter increments for each request in the current window. If the counter exceeds the limit, requests are dropped.
- **Pros:** Simple to implement, memory efficient.
- **Cons:** Spike in traffic at the edges of the window can allow more requests than the limit.

### 4. Sliding Window Log
- **Concept:** Keeps a log of timestamps for each request. When a request comes, old timestamps outside the window are removed. If the log size <= limit, request is allowed.
- **Pros:** Very accurate.
- **Cons:** High memory footprint since it stores all timestamps.

### 5. Sliding Window Counter
- **Concept:** A hybrid of Fixed Window and Sliding Window Log. Uses previous window's counter to estimate the current window's count based on the overlap percentage.
- **Pros:** Smooths out traffic spikes, memory efficient.

## Core Components
1. **RateLimiter (Interface):** Defines the `boolean allowRequest(String clientId)` method.
2. **RuleManager:** Loads and provides rate-limiting rules.
3. **Strategy Implementations:** Classes implementing the algorithms (TokenBucket, FixedWindow, etc.).
