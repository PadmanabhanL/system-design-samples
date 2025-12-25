package com.leetcode.contest.client;

import com.leetcode.contest.bo.Problem;
import com.leetcode.contest.bo.ProblemDetail;
import com.leetcode.contest.bo.Submission;
import com.leetcode.contest.bo.User;
import com.leetcode.contest.service.EvaluationService;
import com.leetcode.contest.service.ProblemService;
import com.leetcode.contest.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LeetCodeContestSubmissionClient {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(100);

    public static void main(String[] args) {

        UserService userService = new UserService();

        ProblemService problemService = ProblemService.getInstance();

        for (int i = 0; i < 10; i++) {
            User user = new User("User"+i, "User"+i, "User"+i);
            userService.addUser(user);
        }

        for (int i = 0; i < 10; i++) {
            String problemId = String.valueOf(i);
            List<ProblemDetail> problemDetails = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                ProblemDetail inputFile = new ProblemDetail(UUID.randomUUID().toString(), problemId, "input"+j+".txt", new Timestamp(System.currentTimeMillis()), null);
                problemDetails.add(inputFile);
                ProblemDetail outputFile = new ProblemDetail(UUID.randomUUID().toString(), problemId, "output"+j+".txt", new Timestamp(System.currentTimeMillis()), null);
                problemDetails.add(outputFile);
            }
            Problem problem = new Problem(problemId, problemId, problemId,
                    Timestamp.valueOf(LocalDateTime.now()), null, problemDetails);
            problemService.addProblem(problem);
        }

        Submission submission = new Submission(UUID.randomUUID().toString(), "User0", "2", Timestamp.valueOf(LocalDateTime.now()), "Some Solution Code");

        EvaluationService evaluationService = EvaluationService.getInstance();

        List<Runnable> tasks = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);
        evaluationService.evaluateSubmission(submission);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < 1; i++) {
            tasks.add(() -> evaluationService.evaluateSubmission(submission));
        }

        long start = System.currentTimeMillis();
        tasks.forEach(task -> {
            executorService.submit(task);
            counter.incrementAndGet();
        });

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
            long end = System.currentTimeMillis();
            System.out.println("Time taken: " + (end - start));
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            System.out.println("Number of Completed tasks: " + counter.get());
        }


    }
}
