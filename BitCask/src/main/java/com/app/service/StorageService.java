package com.app.service;

import com.app.index.bo.KeyValueMetadata;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageService {

    private final IndexService indexService;

    private final FileWriterService fileWriterService;

    private final ExecutorService executorService;

    private final FileRotationService fileRotationService;

    public StorageService() {
        this.fileRotationService = new FileRotationService();
        this.fileWriterService = new FileWriterService(fileRotationService);
        this.indexService = new IndexService();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void save(String key, String value) {
        CompletableFuture.runAsync(() -> {
            doSave(key, value);
        }, executorService);
    }

    private void doSave(String key, String value) {
        KeyValueMetadata keyValueMetadata = fileWriterService.saveAndRotate(key, value, this.fileRotationService.getAppendOnlyFile());
        indexService.getIndex().put(key, keyValueMetadata);
        if (value != null && value.equals("-1")) {
            indexService.getIndex().remove(key);
        }
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

    public FileWriterService getFileWriterService() {
        return fileWriterService;
    }
}
