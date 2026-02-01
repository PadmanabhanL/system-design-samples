package com.practice.thread;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.practice.thread.Inventory.MAX_ITEMS;

/***
 * Demo for need for locking or synchronized irrespective of using AtomicInteger
 */

public class AtomicLimitationDemo {

    private static final ExecutorService executor = Executors.newFixedThreadPool(15);

    public static void main(String[] args) throws InterruptedException {

        Inventory inventory = new Inventory();
        Instant start = Instant.now();
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                //inventory.unsafeBookingOfItem();
                // inventory.safeBookingOfItemWithSynchronizedMethod();
               // inventory.safeBookingOfItemWithSynchronizedBlockUsingThis();
               // inventory.safeBookingOfItemWithSynchronizedBlockUsingClass();
                //inventory.safeBookingOfItemWithSynchronizedBlockUsingClass();
                //inventory.safeBookingOfItemWithLock();
                inventory.safeBookingOfItemWithSemaphore();

            });
        }
        executor.shutdown();
        executor.awaitTermination(15, TimeUnit.SECONDS);
        Instant end = Instant.now();
        System.out.println("Time Taken:" + ChronoUnit.SECONDS.between(start, end) + " Seconds");


        System.out.println("Final Inventory Sold: " + inventory.getItemsSold() + " while available was "+ MAX_ITEMS);
        // EXPECTED: 5
        // ACTUAL: Often 6, 7, or 8 (Overbooking!)
    }
}
