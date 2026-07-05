package com.lld.cache;

public class DoublyLinkedList<E> {
    private Node<E> head;
    private Node<E> tail;

    public DoublyLinkedList() {
        this.head = new Node<>(null);
        this.tail = new Node<>(null);
        head.setNext(tail);
        tail.setPrev(head);
    }

    public void detachNode(Node<E> node) {
        if (node != null) {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
    }

    public void addNodeAtLast(Node<E> node) {
        Node<E> prev = tail.getPrev();
        prev.setNext(node);
        node.setPrev(prev);
        node.setNext(tail);
        tail.setPrev(node);
    }

    public Node<E> addElementAtLast(E element) {
        Node<E> newNode = new Node<>(element);
        addNodeAtLast(newNode);
        return newNode;
    }

    public Node<E> getFirstNode() {
        if (head.getNext() == tail) {
            return null; // Empty list
        }
        return head.getNext();
    }
}
