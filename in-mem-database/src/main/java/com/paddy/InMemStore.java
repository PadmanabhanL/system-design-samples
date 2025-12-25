package com.paddy;

public interface InMemStore<U, V> {

    void add(U key, V value);

    void remove(U key);

    V get(U key);

    void add(U key, V value, Long ttl);

    boolean contains(U key);


}
