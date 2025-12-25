package com.leetcode.contest.bo;

import java.sql.Timestamp;

public record ProblemDetail(String problemDetailId, String problemId, String fileName, Timestamp createdAt, Timestamp updatedAt) {
}
