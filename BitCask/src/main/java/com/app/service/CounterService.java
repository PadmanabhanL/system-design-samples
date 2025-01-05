package com.app.service;

public class CounterService {

    private static int counter = 0;

    public static int incrementCounterForRegularFile() {
        counter += 1;
        return counter;
    }

    public static int incrementCounterForMergeFile() {
        counter += 10;
        return counter;
    }

    public static int getCounter() {
        return counter;
    }
}
