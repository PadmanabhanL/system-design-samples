package com.app.service;

import com.app.index.bo.KeyValueMetadata;

import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IndexService {

    private final Map<String, KeyValueMetadata> index;

    private final FileRotationService fileRotationService;

    public IndexService(FileRotationService fileRotationService) {
        this.index = new ConcurrentHashMap<>();
        this.fileRotationService = fileRotationService;
    }

    public Map<String, KeyValueMetadata> getIndex() {
        return this.index;
    }

    public String findValueByKey(String key) {
        if (!index.containsKey(key)) {
            return "Unable to find value";
        }
        long startTime = System.currentTimeMillis();
        try {
            KeyValueMetadata valueMetadata = index.get(key);
            RandomAccessFile randomAccessFile = new RandomAccessFile(fileRotationService.getFilePath() + valueMetadata.getFileName(), "r");

            randomAccessFile.seek(valueMetadata.getValueByteOffset());
            byte[] buffer = new byte[valueMetadata.getValueByteLength()];

            randomAccessFile.read(buffer);

            return new String(buffer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("Time Taken to Find:" + (endTime - startTime) + "ms");
        }
    }
}
