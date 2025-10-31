package com.system.bo;

import java.util.List;

public record Node(String nodeId, String nodeName, List<User> users) {
    public void add(User user){
        users.add(user);
    }

    public void remove(User user){
        users.remove(user);
    }

    public User find(String userId){
        return users.stream().filter(user -> user.userId().equals(userId)).findFirst().orElse(null);
    }

    public List<User> findAll(){
        return users;
    }

}