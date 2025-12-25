package com.leetcode.contest.service;

import com.leetcode.contest.bo.Problem;
import com.leetcode.contest.dao.ProblemDao;

import java.util.List;

public class ProblemService {

    private final ProblemDao problemDao;

    private ProblemService() {
        this.problemDao = new  ProblemDao();
    }

    private static ProblemService problemService;

    public static ProblemService getInstance() {
        if (problemService == null) {
            synchronized (EvaluationService.class) {
                if (problemService == null) {
                    problemService = new ProblemService();
                }
            }
        }
        return problemService;
    }

    public List<Problem> getAllProblems() {
        return problemDao.getProblems();
    }

    public void addProblem(Problem problem) {
        problemDao.addProblem(problem);
    }

    public void updateProblem(Problem problem) {
        problemDao.updateProblem(problem);
    }

    public Problem getProblemById(String id) {
        return problemDao.getProblem(id);
    }
}
