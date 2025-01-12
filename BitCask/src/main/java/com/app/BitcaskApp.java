package com.app;

import com.app.service.IndexService;
import com.app.service.MergeAndCompactionService;
import com.app.service.StorageService;

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

        IndexService indexService = service.getIndexService();
        indexService.getIndex().forEach((key, v) -> {
            System.out.println(key + " "+indexService.findValueByKey(key));
        });


        /*System.out.println(service.getIndexService().getIndex().get("key0"));

        System.out.println(service.find("key0"));*/



      /*  while (true) {

        }*/
       // service.delete("key988");

    }

    private void initiateMergeAndCompaction(StorageService service) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        MergeAndCompactionService mergeAndCompactionService = new MergeAndCompactionService(service.getFileRotationService(),
                                                                                            service.getIndexService());
        mergeAndCompactionService.mergeAndCompact();
    }
}
