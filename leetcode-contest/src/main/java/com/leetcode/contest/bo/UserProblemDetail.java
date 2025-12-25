package com.leetcode.contest.bo;

import java.sql.Timestamp;

public record UserProblemDetail(String userId, String problemId, Timestamp attemptedAt, Timestamp solvedAt) {
}
