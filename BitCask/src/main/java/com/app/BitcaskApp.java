package com.app;

import com.app.service.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class BitcaskApp {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(100);

    private static CountDownLatch latch = new CountDownLatch(1000_000_000);

    public static void main(String[] args) {
        //int loop size 1000_00

        BitcaskApp app = new BitcaskApp();


        StorageService service = new StorageService();

        app.initiateMergeAndCompaction(service);

        long startTime = System.nanoTime();
        for (int i = 0; i < 1000_000; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    service.save("key" + index, "value" + index);
                } finally {
                    latch.countDown();
                }
            });
        }

        for (int i = 0; i < 1000; i++) {
            final int ind = i;
            executorService.submit(() -> {
                System.out.println(service.find("key"+ind));
            });
        }

        for (int i = 0; i < 1000_00; i++) {
            if (i % 7 == 0) {
                final int index = i;
                executorService.submit(() -> {
                    service.delete("key"+index);
                });
            }
        }



        try {
            latch.await();
            long endTime = System.nanoTime();
            System.out.println("Total time taken (including nested async tasks): " + (endTime - startTime) / 1_000_000 + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void initiateMergeAndCompaction(StorageService service) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        FileRotationService fileRotationService = service.getFileRotationService();
        IndexService indexService = service.getIndexService();
        FileWriterService fileWriterService = service.getFileWriterService();
        System.out.println("Hash For FileRotationService: " + fileRotationService);
        MergeAndCompactionService mergeAndCompactionService = new MergeAndCompactionService(fileRotationService,
                indexService, fileWriterService);
        mergeAndCompactionService.mergeAndCompact();
    }
}
