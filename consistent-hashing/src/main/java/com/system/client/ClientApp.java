package com.system.client;

import com.system.bo.Node;
import com.system.bo.User;
import com.system.bo.UserData;
import com.system.service.AppConstants;
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
        Node node6 = new Node(UUID.randomUUID().toString(), "Node6", new ArrayList<>());
        Node node7 = new Node(UUID.randomUUID().toString(), "Node7", new ArrayList<>());
        Node node8 = new Node(UUID.randomUUID().toString(), "Node8", new ArrayList<>());
        Node node9 = new Node(UUID.randomUUID().toString(), "Node9", new ArrayList<>());

        nodes.add(node1);
        nodes.add(node2);
        nodes.add(node3);
        nodes.add(node4);
        nodes.add(node5);

        nodes.add(node6);
        nodes.add(node7);
        nodes.add(node8);
        nodes.add(node9);

        consistentHashingService.addNode(node1);
        consistentHashingService.addNode(node2);
        consistentHashingService.addNode(node3);
        consistentHashingService.addNode(node4);

        Node[] ringBuffer = consistentHashingService.getRingBuffer();
        for (int i = 0; i < ringBuffer.length; i++) {
            if (ringBuffer[i] != null) {
                System.out.println("Index " + i +" has Node "+ ringBuffer[i].nodeName());
            }

        }

        List<User> users = new ArrayList<>();
        for (int i = 0; i < AppConstants.USER_COUNT; i++) {
            List<UserData> userDataList = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                UserData userData = new UserData(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
                userDataList.add(userData);
            }
            User user = new User("User"+i, userDataList, true);
            users.add(user);
        }

        for (User user : users) {
            consistentHashingService.addUser(user);
        }

        displayNodeStatus(nodes);

        consistentHashingService.addFreshNode(node5);

        displayNodeStatus(nodes);

        consistentHashingService.removeNode(node2);
        consistentHashingService.removeNode(node3);

        displayNodeStatus(nodes);


        consistentHashingService.addFreshNode(node6);
        consistentHashingService.addFreshNode(node7);
        consistentHashingService.addFreshNode(node8);
        consistentHashingService.addFreshNode(node9);

        displayNodeStatus(nodes);

        String userID = "User1";

        System.out.println(userID + " can be found in:");
        for (Node node : nodes) {
            if (node.users().stream().filter(user -> user.getUserId().equals(userID)).findFirst().isPresent()) {
                System.out.println("node :"+ node.nodeName());
            }
        }

        for (int i = 0; i < ringBuffer.length; i++) {
            if (ringBuffer[i] != null) {
                System.out.println("Index " + i +" has Node "+ ringBuffer[i].nodeName());
            }

        }

        System.out.println(consistentHashingService.findUserData(userID));


    }

    private static void displayNodeStatus(List<Node> nodes) {
        for (Node node : nodes) {
            System.out.println(node.nodeName() + " UserCount:" + node.findAll().size());
            System.out.println("=================================================");
            for (User user : node.users()) {
                System.out.println(user.getUserId());
            }
            System.out.println("--------------------------------------------------");
        }
    }


}
