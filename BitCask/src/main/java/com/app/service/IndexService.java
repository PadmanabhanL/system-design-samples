package com.app.service;

import com.app.common.StartupListener;
import com.app.index.bo.KeyValueMetadata;

import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class IndexService {

    private final Map<String, KeyValueMetadata> index;

    private final Properties properties;

    public IndexService() {
        this.index = new ConcurrentHashMap<>();
        this.properties = StartupListener.getProperties();
    }

    public Map<String, KeyValueMetadata> getIndex() {
        return this.index;
    }

    public String findValueByKey(String key) {
        if (!this.index.containsKey(key)) {
            return "Unable to find value";
        }
        long startTime = System.currentTimeMillis();
        try {
            KeyValueMetadata valueMetadata = this.index.get(key);
            RandomAccessFile randomAccessFile = new RandomAccessFile(properties.getProperty("storage-path") + valueMetadata.getFileName(), "r");

            randomAccessFile.seek(valueMetadata.getValueByteOffset());
            byte[] buffer = new byte[Math.toIntExact(valueMetadata.getValueByteLength())];

            randomAccessFile.read(buffer);

            String value = new String(buffer);
            return value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long endTime = System.currentTimeMillis();
            //System.out.println("Time Taken to Find:" + (endTime - startTime) + "ms");
        }
    }
}
