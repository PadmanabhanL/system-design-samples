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
        return users.stream().filter(user -> user.getUserId().equals(userId)).findFirst().orElse(null);
    }

    public List<User> findAll(){
        return users;
    }

    public void remove(List<User> usersToBeRemoved){
        users.removeAll(usersToBeRemoved);
    }

    public void add(List<User> usersToBeAdded){
        users.addAll(usersToBeAdded);
    }

}