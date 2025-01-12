package com.app.service;

import com.app.index.bo.KeyValueMetadata;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageService {

    private final IndexService indexService;

    private final FileRotationService fileRotationService;

    private final ExecutorService executorService;

    public StorageService() {
        this.fileRotationService = new FileRotationService();
        this.indexService = new IndexService(fileRotationService);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void save(String key, String value) {

        CompletableFuture.runAsync(() -> {
            KeyValueMetadata keyValueMetadata = fileRotationService.saveAndRotate(key, value, false);

            indexService.getIndex().put(key, keyValueMetadata);

            if (value != null && value.equals("-1")) {
                indexService.getIndex().remove(key);
            }
        }, executorService);
    }

    public void delete(String key) {
        if (indexService.getIndex().containsKey(key)) {
            save(key, "-1");
        }
    }

    public String find(String key) {
        return indexService.findValueByKey(key);
    }

    public FileRotationService getFileRotationService() {
        return fileRotationService;
    }

    public IndexService getIndexService() {
        return indexService;
    }
}
