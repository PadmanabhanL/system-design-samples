package com.leetcode.contest.bo;

import java.sql.Timestamp;

public record InputOutputPair(String inputFileContent, Timestamp inputFileUpdatedAt, String outputFileContent, Timestamp outputFileUpdatedAt) {
}
