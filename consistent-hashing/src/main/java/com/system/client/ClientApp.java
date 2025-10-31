package com.system.client;

import com.system.bo.Node;
import com.system.bo.User;
import com.system.bo.UserData;
import com.system.service.ConsistentHashingService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientApp {

    public static void main(String[] args) {
        ConsistentHashingService consistentHashingService = new ConsistentHashingService();

        List<Node> nodes = new ArrayList<>();

        Node node1 = new Node(UUID.randomUUID().toString(), "Node1", new ArrayList<>());
        Node node2 = new Node(UUID.randomUUID().toString(), "Node2", new ArrayList<>());
        Node node3 = new Node(UUID.randomUUID().toString(), "Node3", new ArrayList<>());
        Node node4 = new Node(UUID.randomUUID().toString(), "Node4", new ArrayList<>());
        Node node5 = new Node(UUID.randomUUID().toString(), "Node5", new ArrayList<>());

        nodes.add(node1);
        nodes.add(node2);
        nodes.add(node3);
        nodes.add(node4);
        nodes.add(node5);

        consistentHashingService.addNode(node1);
        consistentHashingService.addNode(node2);
        consistentHashingService.addNode(node3);
        consistentHashingService.addNode(node4);
        consistentHashingService.addNode(node5);

        Node[] ringBuffer = consistentHashingService.getRingBuffer();
        for (int i = 0; i < ringBuffer.length; i++) {
            System.out.println("Index " + i +" has Node "+ (ringBuffer[i] == null ? "UNASSIGNED" : ringBuffer[i].nodeName()));

        }

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<UserData> userDataList = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                UserData userData = new UserData(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
                userDataList.add(userData);
            }
            User user = new User("User"+i, userDataList);
            users.add(user);
        }

        for (User user : users) {
            consistentHashingService.addUser(user);
        }

        for (Node node : nodes) {
            System.out.println(node.nodeName());
            System.out.println("=================================================");
            for (User user : node.users()) {
                System.out.println(user.userId());
            }
            System.out.println("--------------------------------------------------");
        }
    }
}
