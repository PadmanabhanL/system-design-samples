package com.app;

import com.app.service.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BitcaskApp {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(100);
    public static void main(String[] args) {
        //int loop size 1000_00

        BitcaskApp app = new BitcaskApp();

        StorageService service = new StorageService();
        for (int i = 0; i < 1000_00; i++) {
            final int index = i;
            executorService.submit(() -> {
                service.save("key"+index, "value"+index);
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

        app.initiateMergeAndCompaction(service);
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
