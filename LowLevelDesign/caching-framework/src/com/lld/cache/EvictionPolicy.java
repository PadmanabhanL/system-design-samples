package com.lld.cache;

public interface EvictionPolicy<K> {
    void keyAccessed(K key);
    K evictKey();
}
