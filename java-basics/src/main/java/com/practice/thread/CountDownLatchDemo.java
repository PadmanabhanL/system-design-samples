package com.practice.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountDownLatchDemo {

    private final CountDownLatch countDownLatch = new CountDownLatch(3);

    public static void main(String[] args) {

        CountDownLatchDemo demo = new CountDownLatchDemo();
        demo.initiateSample();
    }

    private void initiateSample() {
        new Thread(this::readData).start();
        new Thread(this::updateData).start();

        new Thread(this::emailExternalUser).start();

        try {
            //countDownLatch.await();//Indefinite Waiting
            countDownLatch.await(2, TimeUnit.SECONDS);
            System.out.println("All tasks completed!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread interrupted while waiting");
        }

    }

    private void readData() {
        try {
            System.out.println("Reading Data From External System...");
        } finally {
            countDownLatch.countDown();
        }
    }

    private void updateData() {
        try {
            System.out.println("Updating Data From External System...");
        } finally {
            countDownLatch.countDown();
        }
    }

    private void emailExternalUser() {
        try {
            Thread.sleep(15000);
            System.out.println("Email External User...");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Email thread interrupted while waiting");
        } finally {
            countDownLatch.countDown();
        }
    }

}
