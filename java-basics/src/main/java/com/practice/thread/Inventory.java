package com.practice.thread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Inventory {

    private AtomicInteger itemsSold = new AtomicInteger(0);
    public static final int MAX_ITEMS = 5;


    public int getItemsSold() {
        return itemsSold.get();
    }

    public void unsafeBookingOfItem() {
        if (itemsSold.get() < MAX_ITEMS) {
            System.out.println("Item booked!");
            itemsSold.incrementAndGet();
            System.out.println("I'm going to send a notification!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void safeBookingOfItemWithSynchronizedMethod() {
        if (itemsSold.get() < MAX_ITEMS) {
            System.out.println("Item booked!");
            itemsSold.incrementAndGet();
            System.out.println("I'm going to send a notification!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void safeBookingOfItemWithSynchronizedBlockUsingThis() {
        boolean booked = false;
        synchronized (this) {
            if (itemsSold.get() < MAX_ITEMS) {
                System.out.println("Item booked!");
                itemsSold.incrementAndGet();
                booked = true;
            }
        }
        if (booked) {
            System.out.println("I'm going to send a notification!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Use this ONLY when the shared object is static and there could be multiple instances of Inventory object calling this
    public void safeBookingOfItemWithSynchronizedBlockUsingClass() {
        boolean booked = false;
        synchronized (Inventory.class) {
            if (itemsSold.get() < MAX_ITEMS) {
                System.out.println("Item booked!");
                itemsSold.incrementAndGet();
                booked = true;
            }
        }
        if (booked) {
            System.out.println("I'm going to send a notification!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final Lock lock = new ReentrantLock();
    public void safeBookingOfItemWithLock() {
        boolean booked = false;
        try {
            lock.lock();
            if (itemsSold.get() < MAX_ITEMS) {
                System.out.println("Item booked!");
                itemsSold.incrementAndGet();
                booked = true;
            }

        } finally {
            lock.unlock();
        }
        if (booked) {
            System.out.println("I'm going to send a notification!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final Semaphore semaphore = new Semaphore(1);
    public void safeBookingOfItemWithSemaphore() {
        boolean booked = false;

        try {
            if (semaphore.tryAcquire(100, TimeUnit.MILLISECONDS)) {
                try {
                    if (itemsSold.get() < MAX_ITEMS) {
                        System.out.println("Item booked!");
                        itemsSold.incrementAndGet();
                        booked = true;
                    }
                } finally {
                    semaphore.release();
                }
            } else {
                System.out.println("Not able to acquire semaphore!");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (booked) {
            System.out.println("I'm going to send a notification!");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
