package com.leetcode.contest.dao;

import com.leetcode.contest.bo.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private final List<User> users;

    public UserDao() {
        this.users = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUser(String userId) {
        return users.stream().filter(user -> user.userId().equals(userId)).findFirst().orElse(null);
    }

    public void addUser(User user) {
        users.add(user);
    }
}
