package com.lld.cache;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Testing LRU Cache ---");
        EvictionPolicy<Integer> lruPolicy = new LRUEvictionPolicy<>();
        Cache<Integer, String> cache = new CacheImpl<>(3, lruPolicy);

        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        
        System.out.println("Get 1: " + cache.get(1)); // Access 1, making it most recently used
        
        cache.put(4, "Four"); // Should evict 2 (least recently used)
        
        System.out.println("Get 2 (should be null): " + cache.get(2));
        System.out.println("Get 3: " + cache.get(3)); // Access 3
        
        cache.put(5, "Five"); // Should evict 4 (least recently used)
        
        System.out.println("Get 4 (should be null): " + cache.get(4));
        System.out.println("Get 5: " + cache.get(5));
        System.out.println("Get 1: " + cache.get(1));
    }
}
