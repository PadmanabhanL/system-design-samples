package com.lld.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheImpl<K, V> implements Cache<K, V> {
    private final int capacity;
    private final Map<K, V> storage;
    private final EvictionPolicy<K> evictionPolicy;

    public CacheImpl(int capacity, EvictionPolicy<K> evictionPolicy) {
        this.capacity = capacity;
        this.storage = new HashMap<>();
        this.evictionPolicy = evictionPolicy;
    }

    @Override
    public void put(K key, V value) {
        if (storage.containsKey(key)) {
            storage.put(key, value);
            evictionPolicy.keyAccessed(key);
            return;
        }

        if (storage.size() == capacity) {
            K evictedKey = evictionPolicy.evictKey();
            if (evictedKey != null) {
                storage.remove(evictedKey);
                System.out.println("Evicted key: " + evictedKey);
            }
        }

        storage.put(key, value);
        evictionPolicy.keyAccessed(key);
    }

    @Override
    public V get(K key) {
        if (storage.containsKey(key)) {
            evictionPolicy.keyAccessed(key);
            return storage.get(key);
        }
        return null;
    }

    @Override
    public void delete(K key) {
        if (storage.containsKey(key)) {
            storage.remove(key);
            // In a fully robust implementation, we might need to remove from eviction policy too
            // though depending on the policy, leaving it might be harmless (e.g. LRU will eventually evict the dummy node).
            // For simplicity, we just remove from storage here.
        }
    }

    @Override
    public void clear() {
        storage.clear();
        // Clear eviction policy state in a fully featured system.
    }
}
