package com.paddy.leetcode;

import java.util.HashSet;
import java.util.Set;

public class DeleteNodesFromLinkedList {

    public ListNode modifiedList(int[] nums, ListNode head) {

        Set<Integer> valuesToBeDeleted = new HashSet<>();
        for (int num: nums) {
            valuesToBeDeleted.add(num);
        }


        ListNode nodeToBeReturned = null;
        ListNode prev = null;

        while (head != null) {

            if (valuesToBeDeleted.contains(head.val)) {
                if (prev != null) {
                    prev.next = head.next;
                    if (nodeToBeReturned == null) {
                        nodeToBeReturned = prev;
                    }
                    head = head.next;
                } else {
                    head = head.next;
                }
            } else {
                if (nodeToBeReturned == null) {
                    nodeToBeReturned = head;
                }
                prev = head;
                head = head.next;
            }
        }



        return nodeToBeReturned;

    }

    private static class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            this.val = x;
        }
        ListNode(int x, ListNode next) {
            this.val = x;
            this.next = next;
        }
    }

    public static void main(String[] args) {
        DeleteNodesFromLinkedList deleteNodesFromLinkedList = new DeleteNodesFromLinkedList();
       /* ListNode five = new ListNode(5, null);
        ListNode four = new ListNode(4, five);
        ListNode three = new ListNode(3, four);
        ListNode two = new ListNode(2, three);
        ListNode one = new ListNode(1, two);*/

        /*ListNode six = new ListNode(2, null);
        ListNode five = new ListNode(1, six);
        ListNode four = new ListNode(2, five);
        ListNode three = new ListNode(1, four);
        ListNode two = new ListNode(2, three);
        ListNode one = new ListNode(1, two);*/

        ListNode four = new ListNode(4, null);
        ListNode three = new ListNode(3, four);
        ListNode two = new ListNode(1, three);
        ListNode one = new ListNode(5, two);

        deleteNodesFromLinkedList.printList(one);

        System.out.println();
        ListNode ref = deleteNodesFromLinkedList.modifiedList(new int[] {9, 4}, one);
        System.out.println("printing:"+ ref);
        deleteNodesFromLinkedList.printList(ref);

    }

    public void printList(ListNode node) {
        while (node != null) {
            System.out.print(node.val + " -> ");
            node = node.next;
        }
    }
}
