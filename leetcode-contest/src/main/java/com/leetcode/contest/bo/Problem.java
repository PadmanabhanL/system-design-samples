package com.leetcode.contest.bo;

import java.sql.Timestamp;
import java.util.List;

public record Problem(String problemId, String title, String description, Timestamp createdAt, Timestamp updatedAt, List<ProblemDetail> problemDetails) {

    public void addProblemDetail(ProblemDetail problemDetail) {
        problemDetails.add(problemDetail);
    }

    public void removeProblemDetail(ProblemDetail problemDetail) {
        problemDetails.remove(problemDetail);
    }
}
