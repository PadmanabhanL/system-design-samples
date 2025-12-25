package com.leetcode.contest.bo;

import java.sql.Timestamp;

public record Submission(String submissionId, String userId, String problemId, Timestamp submittedAt, String code) {
}
