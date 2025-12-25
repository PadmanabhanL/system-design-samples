package com.leetcode.contest.service;

import com.google.gson.Gson;
import com.leetcode.contest.bo.InputOutputPair;
import com.leetcode.contest.bo.Problem;
import com.leetcode.contest.bo.ProblemDetail;
import com.leetcode.contest.bo.Submission;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EvaluationService {

    private static EvaluationService evaluationService;

    private final ProblemService problemService;

    private final Map<String, List<InputOutputPair>> localCache = new ConcurrentHashMap<>();

    private final CloseableHttpClient httpClient;

    private EvaluationService() {
        this.problemService = ProblemService.getInstance();
        this.httpClient = HttpClients.createDefault();
    }

    public static EvaluationService getInstance() {
        if (evaluationService == null) {
            synchronized (EvaluationService.class) {
                if (evaluationService == null) {
                    evaluationService = new EvaluationService();
                }
            }
        }
        return evaluationService;
    }

    public boolean evaluateSubmission(Submission submission) {

        System.out.println("Evaluating submission by user " + submission.userId() + " for problem " + submission.problemId());

        Problem problemById = problemService.getProblemById(submission.problemId());
        if (problemById == null) {
            throw new IllegalArgumentException("Problem with id " + submission.problemId() + " not found");
        }

        if (localCache.containsKey(problemById.problemId())) {
            List<InputOutputPair> inputOutputPairs = localCache.get(problemById.problemId());
            for (InputOutputPair inputOutputPair : inputOutputPairs) {
                if (!executeSubmission(submission, inputOutputPair.inputFileContent(), inputOutputPair.outputFileContent())) {
                    return false;
                }
            }
        } else {

            List<ProblemDetail> problemDetails = problemById.problemDetails();
            List<InputOutputPair> inputOutputPairs = new ArrayList<>();
            for (int i = 0; i < problemDetails.size(); i = i + 2) {
                ProblemDetail inputFile = problemDetails.get(i);
                ProblemDetail outputFile = problemDetails.get(i + 1);

                String inputFileContent = loadFileFromFileStorage(problemById.problemId(), inputFile.fileName());
                String outputFileContent = loadFileFromFileStorage(problemById.problemId(), outputFile.fileName());
                System.out.println("InputFileContent:" + inputFileContent + " OutputFileContent:" + outputFileContent);
                inputOutputPairs.add(new InputOutputPair(inputFileContent,
                        inputFile.updatedAt() == null ? inputFile.createdAt() : inputFile.updatedAt(),
                        outputFileContent,
                        outputFile.updatedAt() == null ? outputFile.createdAt() : outputFile.updatedAt()));
                localCache.put(problemById.problemId(), inputOutputPairs);
            }
            for (InputOutputPair inputOutputPair : inputOutputPairs) {
                if (!executeSubmission(submission, inputOutputPair.inputFileContent(), inputOutputPair.outputFileContent())) {
                    return false;
                }
            }

        }

        return false;

    }

    public boolean executeSubmission(Submission submission, String inputFileContent, String outputFileContent) {
        System.out.println("Executing files against code " + inputFileContent + " and " + outputFileContent );
        return true;
    }

    private String loadFileFromFileStorage(String problemId, String fileName) {

        ClassicHttpRequest classicHttpRequest = new HttpGet("http://localhost:8082/problems/"+ problemId +"/" + fileName);
        try {
            String responseJson = httpClient.execute(classicHttpRequest, new BasicHttpClientResponseHandler());
            return (String) new Gson().fromJson(responseJson, Map.class).get("content");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
