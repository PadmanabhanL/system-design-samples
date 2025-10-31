package com.system.service;

import com.system.bo.Node;
import com.system.bo.User;
import com.system.hashing.function.HashingFunctionFactory;

public class ConsistentHashingService {

    public static final int LIMIT = 25;//0_000_00;

    private final Node[] ringBuffer;

    private final HashingFunctionFactory  hashingFunctionFactory;

    public void addNode(Node node) {
        hashingFunctionFactory.getHashingFunctions().forEach(hashingFunction -> {
            int hash = hashingFunction.hash(node.nodeId());
            System.out.println("Hash for Node is "+hash + " " + node.nodeName());
            ringBuffer[hash] = node;
        });
    }

    public ConsistentHashingService() {

        this.ringBuffer = new Node[LIMIT];
        this.hashingFunctionFactory = new HashingFunctionFactory();
    }

    public Node[] getRingBuffer() {
        return ringBuffer;
    }

    public void addUser(User user) {
        hashingFunctionFactory.getHashingFunctions().forEach(hashingFunction -> {
            int hash = hashingFunction.hash(user.userId());
            System.out.println("Hash for User "+ user.userId()+ " is "+hash);
            if (ringBuffer[hash] == null) {
                for (int i = hash; i < ringBuffer.length; i++) {
                    Node node = ringBuffer[i];
                    if (node != null) {
                        System.out.println("Adding User :" + user.userId() + " to Node " + node.nodeName());
                        node.add(user);
                        break;
                    }
                    if (i == ringBuffer.length - 1) {
                        i = 0;
                    }
                }
            } else {
                ringBuffer[hash].add(user);
                System.out.println("Adding User :" + user.userId() + " to Node " + ringBuffer[hash].nodeName());
            }
        });
    }
}
