package com.basic.client;

import com.basic.lru.LRUCache;

public class LRUClient {

    public static void main(String[] args) {
        LRUCache lruCache = new LRUCache(2);
        lruCache.put(2, 1);
        lruCache.put(1, 1);
        lruCache.put(2, 3);
        lruCache.put(4, 1);

        lruCache.get(1);
        lruCache.get(2);
    }
}
