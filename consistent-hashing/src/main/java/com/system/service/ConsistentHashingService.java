package com.system.service;

import com.system.bo.Node;
import com.system.bo.User;
import com.system.hashing.function.HashingFunctionFactory;

import java.util.ArrayList;
import java.util.List;

public class ConsistentHashingService {

    public static final int LIMIT = 1000;//0_000_00;

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
            int hash = hashingFunction.hash(user.getUserId());
         //   System.out.println("Hash for User "+ user.getUserId()+ " is "+hash);
            if (ringBuffer[hash] == null) {
                for (int i = hash; i < ringBuffer.length; i++) {
                    Node node = ringBuffer[i];
                    if (node != null) {
                      //  System.out.println("Adding User :" + user.getUserId() + " to Node " + node.nodeName());
                        node.add(user);
                        user.addHashes(hash);
                        break;
                    }
                    if (i == ringBuffer.length - 1) {
                        i = 0;
                    }
                }
            } else {
                ringBuffer[hash].add(user);
                user.addHashes(hash);
              //  System.out.println("Adding User :" + user.getUserId() + " to Node " + ringBuffer[hash].nodeName());
            }
        });
    }

    public void addFreshNode(Node node) {
        hashingFunctionFactory.getHashingFunctions().forEach(hashingFunction -> {
           int nodeHash = hashingFunction.hash(node.nodeId());

           int hash = nodeHash;
           System.out.println("Assigning Node "+node.nodeName()+" to hash "+hash);
           ringBuffer[hash] = node;

           while (ringBuffer[++hash] == null) {
               //simply traverse
               if (hash == ringBuffer.length - 1) {
                   hash = 0;
               }
           }
           Node nextNode = ringBuffer[hash];
           List<User> usersToBeRemoved = new ArrayList<>();
            if (nextNode != null) {
                System.out.println("Target Node:" + nextNode.nodeName());
                for (User user: nextNode.findAll()) {
                   int userHash = hashingFunction.hash(user.getUserId());
                    System.out.println("Hash for User "+ user.getUserId()+ " is "+userHash + " " + user.getHashes());
                   if (nodeHash  > userHash && userHash < hash) {
                       usersToBeRemoved.add(user);
                       node.add(user);
                   }
               }

               nextNode.remove(usersToBeRemoved);
           }

        });
    }
}
