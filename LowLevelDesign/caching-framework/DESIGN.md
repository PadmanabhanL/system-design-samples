# Caching Framework - Low Level Design

## Overview
A caching framework is designed to store data temporarily to serve future requests for that data faster. It reduces latency, improves system performance, and minimizes load on backend systems (like databases).

## Requirements

### Functional Requirements
- **Put(key, value):** Insert or update the value for a given key.
- **Get(key):** Retrieve the value associated with a key.
- **Delete(key):** Remove a key-value pair from the cache.
- **Eviction Policies:** Support various eviction algorithms (e.g., LRU, LFU, FIFO) when the cache reaches its capacity.
- **TTL (Time To Live):** Support expiring entries after a specific time duration.

### Non-Functional Requirements
- **Thread-safe:** Safe for concurrent access.
- **Performant:** O(1) time complexity for basic operations (Get/Put).
- **Extensible:** Easy to add new eviction policies.

## Design Patterns Used
- **Strategy Pattern:** For interchangeable eviction algorithms.
- **Factory Pattern:** To create cache instances.
- **Observer/Pub-Sub Pattern (Optional):** To notify components on cache events (eviction, expiration).

## Core Components
1. **Cache (Interface):** Defines the contract (Put, Get, Delete).
2. **CacheManager:** Manages cache instances and their lifecycles.
3. **EvictionPolicy (Interface):** Defines how to choose which element to evict.
    - *LRUEvictionPolicy:* Least Recently Used.
    - *LFUEvictionPolicy:* Least Frequently Used.
4. **Storage:** The underlying data structure holding the cache entries. Usually a `ConcurrentHashMap`.

## Detailed Component Interaction

### LRU Eviction Policy
Implemented using a combination of a Doubly Linked List and a HashMap.
- **HashMap:** Maps keys to Doubly Linked List nodes for O(1) lookup.
- **Doubly Linked List:** Maintains the order of elements based on recent usage. The most recently used element is moved to the head, and the least recently used element is at the tail (which gets evicted when capacity is full).
