package com.practice.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounterDemo {

    public int counter;

    public AtomicInteger atomicCounter = new AtomicInteger(0);

    public void primitiveIncrement(){
        ++counter;
    }

    public int getCounter() {
        return counter;
    }

    public void atomicIncrement() {
        atomicCounter.incrementAndGet();
    }

    public int getAtomicCounter() {
        return atomicCounter.get();
    }
}
