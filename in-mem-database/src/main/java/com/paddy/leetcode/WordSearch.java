package com.paddy.leetcode;

public class WordSearch {
    public boolean exist(char[][] board, String word) {
        int rows = board.length;
        int cols = board[0].length;

        int wordLength = word.length();
        int wordIndex = 0;

        boolean[][] visited = new boolean[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (helper(row, col, rows, cols, word, wordIndex, board, wordLength, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean helper(int row, int col, int rows, int cols, String word, int wordIndex, char[][] board, int wordLength, boolean[][] visited) {

        if (wordIndex >= word.length()) {
            return true;
        }

        if (row >= rows || row < 0 || col >= cols || col < 0 || word.charAt(wordIndex) != board[row][col] || visited[row][col]) {
            return false;
        }

        visited[row][col] = true;

        boolean flag = helper(row + 1, col, rows, cols, word, wordIndex + 1, board, wordLength, visited) ||
                helper(row - 1, col, rows, cols, word, wordIndex + 1, board, wordLength, visited) ||
                helper(row, col + 1, rows, cols, word, wordIndex + 1, board, wordLength, visited) ||
                helper(row, col - 1, rows, cols, word, wordIndex + 1, board, wordLength, visited);

        visited[row][col] = false;

        return flag;


    }

    public static void main(String[] args) {
        WordSearch wordSearch = new WordSearch();
        char[][] board = {
                {'A', 'B', 'C', 'E'},
                {'S', 'F', 'C', 'S'},
                {'A', 'D', 'E', 'E'}
        };

        String word = "ABCCED";

        System.out.println(wordSearch.exist(board, word));
    }

}
