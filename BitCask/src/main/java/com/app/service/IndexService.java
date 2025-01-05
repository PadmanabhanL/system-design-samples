package com.app.service;

import com.app.index.bo.ValueMetadata;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IndexService {

    private final Map<String, ValueMetadata> index;

    private final FileRotationService fileRotationService;

    public IndexService(FileRotationService fileRotationService) {
        this.index = new ConcurrentHashMap<>();
        this.fileRotationService = fileRotationService;
    }

    public Map<String, ValueMetadata> getIndex() {
        return this.index;
    }

    public String findValueByKey(String key) {
        if (!index.containsKey(key)) {
            return "Unable to find value";
        }
        long startTime = System.currentTimeMillis();
        try {
            ValueMetadata valueMetadata = index.get(key);
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileRotationService.getFilePath() + valueMetadata.getFileName(), "r");

            randomAccessFile.seek(valueMetadata.getByteOffset());
            byte[] buffer = new byte[valueMetadata.getByteLength()];

            randomAccessFile.read(buffer);

            String kvPair = new String(buffer);
            return kvPair.isEmpty() ? "" : kvPair.split("\\,")[1];
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("Time Taken to Find:" + (endTime - startTime) + "ms");
        }
    }
}
