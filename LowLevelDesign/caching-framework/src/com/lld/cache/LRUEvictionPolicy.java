package com.lld.cache;

import java.util.HashMap;
import java.util.Map;

public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {
    private final DoublyLinkedList<K> dll;
    private final Map<K, Node<K>> map;

    public LRUEvictionPolicy() {
        this.dll = new DoublyLinkedList<>();
        this.map = new HashMap<>();
    }

    @Override
    public void keyAccessed(K key) {
        if (map.containsKey(key)) {
            dll.detachNode(map.get(key));
            dll.addNodeAtLast(map.get(key));
        } else {
            Node<K> newNode = dll.addElementAtLast(key);
            map.put(key, newNode);
        }
    }

    @Override
    public K evictKey() {
        Node<K> first = dll.getFirstNode();
        if (first == null) {
            return null;
        }
        dll.detachNode(first);
        map.remove(first.getElement());
        return first.getElement();
    }
}
