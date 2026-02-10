package com.sample2;

import java.util.*;

public class SampleApp {

    //Binary tree
    //     1
    //  2     3
    // 4 5  6  7
    //8 9 10  11  12 13


    public static void main(String[] args) {
        TreeNode eight = new TreeNode(8, null, null);
        TreeNode nine = new TreeNode(9, null, null);
        TreeNode ten = new TreeNode(10, null, null);

        TreeNode eleven = new TreeNode(11, null, null);
        TreeNode twelve = new TreeNode(12, null, null);
        TreeNode thirteen = new TreeNode(13, null, null);

        TreeNode seven = new TreeNode(7, null, null);
        TreeNode six =  new TreeNode(6, twelve, thirteen);
        TreeNode five = new TreeNode(5, ten, eleven);
        TreeNode four = new TreeNode(4, eight, nine);
        TreeNode three = new TreeNode(3, six, seven);
        TreeNode two = new TreeNode(2, four, five);
        TreeNode one = new TreeNode(1, two, three);

        SampleApp app = new SampleApp();

        app.zigZagTraversal(one);



    }

    private static class TreeNode {

        int val;
        TreeNode left;
        TreeNode right;

        public TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }


    private void zigZagTraversal(TreeNode root) {

        Queue<TreeNode> queue = new LinkedList<>();

        queue.offer(root);

        boolean leftToRight = true;

        List<Integer> result = new ArrayList<>();

        Stack<Integer> stack = new Stack<>();
        List<Integer> list = new ArrayList<>();

        while (!queue.isEmpty()) {
            int queueSize = queue.size();

            for (int i = 0; i < queueSize; i++) {
                TreeNode node = queue.remove();
                if (leftToRight) {
                    list.add(node.val);
                } else {
                    stack.add(node.val);
                }
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            leftToRight = !leftToRight;
            while (!stack.isEmpty()) {
                result.add(stack.pop());
            }
            while (!list.isEmpty()) {
                result.add(list.remove(0));
            }
        }

        for(int num: result) {
            System.out.print(num + " -> ");
        }

    }



}
