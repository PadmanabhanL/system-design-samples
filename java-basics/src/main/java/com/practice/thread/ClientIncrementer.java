package com.practice.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientIncrementer {

    public static ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        AtomicCounterDemo atomicCounterDemo = new AtomicCounterDemo();
        for (int i = 0; i < 10000; i++) {
            executor.submit(() -> {
                atomicCounterDemo.primitiveIncrement();
                atomicCounterDemo.atomicIncrement();
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Expected: 10000");
        System.out.println("Actual:   " + atomicCounterDemo.getCounter());
        System.out.println("Actual AtomicCounter: " + atomicCounterDemo.getAtomicCounter());

    }
}
