package com.paddy.leetcode;

public class SubArrayDivisibleByNextElement {

    public static void main(String[] args) {

    }

    public int divisibleByNext(int[] nums) {
        int maxLength = Integer.MIN_VALUE;
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int length = nums.length;

        int start = 1;

        int currentLength = 0;
        while (nums[start] % nums[start - 1] == 0) {
            ++currentLength;
            ++start;
        }
        maxLength = Math.max(maxLength, currentLength);

        for (int i = 1; i < length; i++) {
            if (nums[i] % nums[i - 1] == 0) {

            }
        }

        return -1;
    }
}
