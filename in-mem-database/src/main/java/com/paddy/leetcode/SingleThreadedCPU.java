package com.paddy.leetcode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class SingleThreadedCPU {

    public static void main(String[] args) {
        SingleThreadedCPU singleThreadedCPU = new SingleThreadedCPU();
        int[] result = singleThreadedCPU.getOrder(new int[][] {{35,36},{11,7},{15,47},{34,2},{47,19},{16,14},{19,8},{7,34},{38,15},{16,18},{27,22},{7,15},{43,2},{10,5},{5,4},{3,11}});
        for (int res: result) {
            System.out.print(res + " ");
        }
    }

    public int[] getOrder(int[][] tasks) {
        int[] result = new int[tasks.length];

        int completedTasks = 0;

        int numberOfTasks = tasks.length;

        Task[] taskArray = new Task[numberOfTasks];

        for (int i = 0; i < tasks.length; i++) {
            Task task = new Task(i, tasks[i][0], tasks[i][1]);
            taskArray[i] = task;
        }

        Arrays.sort(taskArray, (a, b) -> Integer.compare(a.enqueueTime, b.enqueueTime));

        PriorityQueue<Task> processingTimePriorityQueue = new PriorityQueue<>(Comparator.comparingInt(Task::getProcessingTime)
                .thenComparing(Task::getIndex));

        int currentPointer = 0;

        int currentTime = taskArray[currentPointer].enqueueTime;

        int resultIndex = 0; 
        
        while (completedTasks != numberOfTasks) {

            while (currentPointer < numberOfTasks && currentTime >= taskArray[currentPointer].enqueueTime) {
                processingTimePriorityQueue.offer(taskArray[currentPointer]);
                ++currentPointer;
            }
            if (!processingTimePriorityQueue.isEmpty()) {
                Task taskToBeProcessed = processingTimePriorityQueue.remove();
                currentTime += taskToBeProcessed.processingTime;
                result[resultIndex] = taskToBeProcessed.index;
                ++resultIndex;
                ++completedTasks;
            } else {
                currentTime = taskArray[currentPointer].enqueueTime;
            }
             

                 

        }


        return result;
    }

    private class Task {
        int index;
        int enqueueTime;
        int processingTime;

        public Task(int index, int enqueueTime, int processingTime) {
            this.index = index;
            this.enqueueTime = enqueueTime;
            this.processingTime = processingTime;
        }

        public int getIndex() {
            return index;
        }

        public int getEnqueueTime() {
            return enqueueTime;
        }

        public int getProcessingTime() {
            return processingTime;
        }
    }
}
