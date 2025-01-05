package com.app.service;

import com.app.index.bo.ValueMetadata;

import java.util.Map;

public class StorageService {

    private final IndexService indexService;

    private final FileRotationService fileRotationService;

    public StorageService() {
        this.fileRotationService = new FileRotationService();
        this.indexService = new IndexService(fileRotationService);
    }

    public void save(String key, String value) {

        Map<String, Object> map = fileRotationService.saveAndRotate(key, value, false);

        if (value != null && value.equals("-1")) {
            indexService.getIndex().remove(key);
        }

        indexService.getIndex().put(key, new ValueMetadata((String) map.get("fileName"),
                                                           (int) map.get("byteOffset"),
                                                           (int) map.get("byteLength")));

    }

    public void delete(String key) {
        save(key, "-1");

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
