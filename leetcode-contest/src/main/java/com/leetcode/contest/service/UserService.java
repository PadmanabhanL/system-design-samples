package com.leetcode.contest.service;

import com.leetcode.contest.bo.User;
import com.leetcode.contest.dao.UserDao;

public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public User getUser(String userId) {
        return userDao.getUser(userId);
    }

    public void addUser(User user) {
        userDao.addUser(user);
    }


}
