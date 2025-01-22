package com.basic.lru;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {

    private final Map<Integer, Node> map;

    private final int capacity;

    private final Node dummyHead;

    private final Node dummyTail;

    public LRUCache(int capacity) {
        this.map = new HashMap<>();
        this.capacity = capacity;
        this.dummyHead = new Node(-1, -1, null, null);
        this.dummyTail = new Node(-1, -1, null, null);
        dummyHead.next = dummyTail;
        dummyTail.prev = dummyHead;
    }

    public int get(int key) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            moveNodeToFront(node);
            return node.value;
        }
        return -1;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.next.prev = node.prev;
            node.prev.next = node.next;
            node.prev = null;
            node.next = null;
            node.value = value;
            moveNodeToFront(node);
            map.put(key, node);
        } else if (map.size() < capacity) {
            Node node = new Node(key, value, null, null);
            moveNodeToFront(node);
            map.put(key, node);
        } else {
            evictLRUNode();
            Node node = new Node(key, value, null, null);
            moveNodeToFront(node);
            map.put(key, node);
        }
    }

    private class Node {
        int key;
        int value;
        Node prev;
        Node next;

        public Node(int key, int value, Node prev, Node next) {
            this.key = key;
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    private void moveNodeToFront(Node node) {
        if (dummyHead.next == dummyTail) {
            dummyHead.next = node;
            node.next = dummyTail;
            node.prev = dummyHead;
            dummyTail.prev = node;
        } else {
            if (node.prev != null) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            node.next = dummyHead.next;
            node.next.prev = node;
            node.prev = dummyHead;
            dummyHead.next = node;
        }
        printLinkedList();
    }

    private void evictLRUNode() {
        if (dummyTail.prev != dummyHead) {
            Node node = dummyTail.prev;
            dummyTail.prev = node.prev;
            node.prev.next = dummyTail;
            this.map.remove(node.key);
            node = null;
        }
        printLinkedList();
    }

    private void printLinkedList() {
        Node temp = this.dummyHead;
        while (temp != null) {
            System.out.print(temp.value + " -> ");
            temp = temp.next;
        }
        System.out.println();
    }
}