package com.practice.thread;

import java.util.concurrent.*;

public class CyclicBarrierDemo {

    private static final ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {

        CyclicBarrierDemo cyclicBarrierDemo = new CyclicBarrierDemo();

        for (int i = 0; i < 20; i++) {
            executor.submit(() -> cyclicBarrierDemo.executeSequenceOfTasks());
        }

        executor.shutdown();

    }

    private final CyclicBarrier barrier = new CyclicBarrier(5, ()-> System.out.println("All Threads Completed the task"));

    private void executeSequenceOfTasks() {

        System.out.println("Fetching Data");
        try {
            Thread.sleep((long) (Math.random() * 2000L));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Unable To Fetch Data");
        }
        try {
            barrier.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Broken Barrier Exception");
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Validating Data");
        try {
            Thread.sleep((long) (Math.random() * 1000L));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Unable To Validate Data");
        }
        try {
            barrier.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Broken Barrier Exception");
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }


        System.out.println("Generating Reports");
        try {
            Thread.sleep((long) (Math.random() * 1000L));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Unable To Validate Data");
        }
        try {
            barrier.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Broken Barrier Exception");
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }

}
