package com.basic.sieve;

import java.util.HashMap;
import java.util.Map;

public class SieveCache {

    private final int capacity;

    private Node head;


    private Node tail;

    private int size = 0;

    private final Map<Integer, Node> map;

    private Node hand;

    public SieveCache(int capacity) {
        this.capacity = capacity;
        this.head = null;
        this.tail = null;
        this.map = new HashMap<>();
    }

    public void addToCache(int key, int value) {
        if (size == capacity) {

            this.hand = this.tail;

            while (this.hand.isVisited()) {
                this.hand.visited = false;
                this.hand = this.hand.prev;


                if (this.hand == null) {
                    this.hand = this.tail;
                }
            }
            //Node to be evicted could be a head or tail or middle node. Handling should be done for all cases
            int valueToBeEvicted = this.hand.value;
            if (this.hand == this.head) {
                this.head = this.head.next;
                if (this.head != null) {
                    this.head.prev = null;
                }
            } else if (this.hand == this.tail) {
                Node target = this.tail.prev;
                if (target != null) {
                    target.next = null;
                    this.tail = target;
                }
            } else {
                Node leftTarget = this.hand.prev;
                Node rightTarget = this.hand.next;
                if (leftTarget != null) {
                    leftTarget.next = rightTarget;
                }
                if (rightTarget != null) {
                    rightTarget.prev = leftTarget;
                }
            }

            this.map.entrySet().removeIf(entry -> entry.getValue().getValue() == valueToBeEvicted);

            --size;
        }
        Node node = new Node(value, null, null);
        if (this.head == null && this.tail == null) {
            this.head = node;
            this.tail = node;
        } else {
            node.next = this.head;
            this.head.prev = node;
            head = node;
            node.visited = false;
        }
        ++size;
        this.map.put(key, node);

        printList();
    }

    public int findValueByKey(int key) {
        if (this.map.containsKey(key)) {
            Node temp = this.map.get(key);
            temp.setVisited(true);
            return temp.getValue();
        }
        return -1;
    }

    public void printList() {
        Node temp = this.head;
        while (temp != null) {
            System.out.print(temp.value +" ( " + temp.isVisited() +" )  -> ") ;
            temp = temp.next;
        }
        System.out.println();
        System.out.println("Map Size:" + this.map.size());
    }


}
