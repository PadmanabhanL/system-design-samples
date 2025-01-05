package com.app;

import com.app.service.MergeAndCompactionService;
import com.app.service.StorageService;

public class BitcaskApp {

    public static void main(String[] args) {
        //int loop size 1000_00

        BitcaskApp app = new BitcaskApp();

        StorageService service = new StorageService();
        for (int i = 0; i < 1000; i++) {
            service.save("key"+i, "value"+i);
        }

        for (int i = 0; i < 1000; i++) {
            if (i % 7 == 0) {
                service.delete("key"+i);
            }
        }

        app.initiateMergeAndCompaction(service);


        System.out.println(service.getIndexService().getIndex().get("key0"));

        System.out.println(service.find("key0"));



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
