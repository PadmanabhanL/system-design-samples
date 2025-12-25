package com.leetcode.contest.dao;

import com.leetcode.contest.bo.Problem;

import java.util.ArrayList;
import java.util.List;

public class ProblemDao {

    private final List<Problem> problems;

    public ProblemDao() {
        this.problems = new ArrayList<>();
    }

    public List<Problem> getProblems() {
        return problems;
    }

    public Problem getProblem(String id) {
        return this.problems.stream().filter(problem -> id.equals(problem.problemId())).findFirst().orElse(null);
    }

    public void addProblem(Problem problem) {
        this.problems.add(problem);
    }

    public void updateProblem(Problem problem) {
        this.problems.removeIf(p -> p.problemId().equals(problem.problemId()));
        this.problems.add(problem);
    }
}
