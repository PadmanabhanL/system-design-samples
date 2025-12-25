package com.leetcode.contest.dao;

import com.leetcode.contest.bo.UserProblemDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserProblemDetailDao {

    private final Map<String, List<UserProblemDetail>> userProblemDetails;

    public UserProblemDetailDao() {
        this.userProblemDetails = new ConcurrentHashMap<>();
    }

    public List<UserProblemDetail> getUserProblemDetails(String userId) {
        return this.userProblemDetails.getOrDefault(userId, new ArrayList<>());
    }

    public void addUserProblemDetails(String userId, UserProblemDetail userProblemDetail) {
        this.userProblemDetails.computeIfAbsent(userId, k -> new ArrayList<>()).add(userProblemDetail);
    }


}
