package com.basic.client;

import com.basic.sieve.SieveCache;

public class SieveClient {

    public static void main(String[] args) {
        SieveCache sieveCache = new SieveCache(4);
        SieveClient clientApp = new SieveClient();

        clientApp.happyPath(sieveCache);
        //clientApp.allAddedAllAccessed(sieveCache);


    }

    private void happyPath(SieveCache sieveCache) {
        sieveCache.addToCache(7, 7);
        System.out.println(sieveCache.findValueByKey(7));
        sieveCache.printList();
        sieveCache.addToCache(5, 5);
        System.out.println(sieveCache.findValueByKey(5));
        sieveCache.printList();
        sieveCache.addToCache(6, 6);
        sieveCache.addToCache(10, 10);
        sieveCache.addToCache(9, 9);
        sieveCache.addToCache(1, 1);
        System.out.println(sieveCache.findValueByKey(5));
        sieveCache.printList();
        sieveCache.addToCache(20, 20);
    }

    private void allAddedAllAccessed(SieveCache sieveCache) {

        sieveCache.addToCache(1, 1);
        sieveCache.addToCache(2, 2);
        sieveCache.addToCache(3, 3);
        sieveCache.addToCache(4, 4);

        sieveCache.findValueByKey(4);
        sieveCache.findValueByKey(3);
        sieveCache.findValueByKey(2);
        sieveCache.findValueByKey(1);

        sieveCache.printList();

        sieveCache.addToCache(5, 5);

        sieveCache.printList();

        sieveCache.addToCache(6, 6);
    }
}
