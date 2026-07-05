package com.lld.cache;

/**
 * Interface defining the standard operations for a Cache.
 * @param <K> Type of the key
 * @param <V> Type of the value
 */
public interface Cache<K, V> {
    void put(K key, V value);
    V get(K key);
    void delete(K key);
    void clear();
}
