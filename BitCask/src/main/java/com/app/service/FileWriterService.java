package com.app.service;

import com.app.common.StartupListener;
import com.app.index.bo.KeyValueMetadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileWriterService {

    private final AtomicLong totalBytesSize;

    private final AtomicLong totalMergeFileBytesSize;

    private final Properties properties;

    private final FileRotationService fileRotationService;

    private final Lock lock = new ReentrantLock();

    public FileWriterService(FileRotationService fileRotationService) {
        this.totalBytesSize = new AtomicLong(0L);
        this.totalMergeFileBytesSize = new AtomicLong(0L);
        this.properties = StartupListener.getProperties();
        this.fileRotationService = fileRotationService;
    }


    public KeyValueMetadata saveAndRotate(String key, String value, File targetFile) {
        try {
            writeKeyValueToFile(key, value, targetFile);
        } catch (IOException ignored) {
        }
        try {
            lock.lock();
            long keyByteOffset = totalBytesSize.get();
            long keyByteLength = key.getBytes().length;
            long valueByteOffset = keyByteOffset + keyByteLength + ",".getBytes().length;
            long valueByteLength = value.getBytes().length;
            long totalBytesToBeWritten = keyByteLength + ",".getBytes().length + valueByteLength + "\n".getBytes().length;
            totalBytesSize.addAndGet(totalBytesToBeWritten);
            return new KeyValueMetadata(targetFile.getName(), keyByteOffset, keyByteLength, valueByteOffset, valueByteLength);
        } finally {
            int maxFileSize = Integer.parseInt((String) properties.get("max-file-size-in-mb"));
            if ((totalBytesSize.get() * 10/ (1024 * 1024)) >= maxFileSize) {
                fileRotationService.rotate(targetFile);
                totalBytesSize.set(0L);
            }
            lock.unlock();
        }
    }

    private void writeKeyValueToFile(String key, String value, File targetFile) throws IOException {
        FileWriter fileWriter = new FileWriter(targetFile, true);
        fileWriter.write(key + "," + value + "\n");
        fileWriter.close();
    }

    public KeyValueMetadata saveAndRotateForMerge(String key, String value, File targetFile) {

        System.out.println("Merge Task Target File:" + targetFile.getAbsolutePath() + " " + key + " " + value);

        long keyByteOffset = totalMergeFileBytesSize.get();

        long keyByteLength = 0;

        long valueByteOffset = 0;

        long valueByteLength = 0;

        long totalBytesToBeWritten = 0;

        try {
            FileWriter fileWriter = new FileWriter(targetFile, true);
            keyByteLength += key.getBytes().length;

            totalBytesToBeWritten += keyByteLength;

            totalBytesToBeWritten += ",".getBytes().length;

            valueByteOffset = totalBytesToBeWritten;

            valueByteLength = value.getBytes().length;

            totalBytesToBeWritten += valueByteLength;

            totalBytesToBeWritten += "\n".getBytes().length;

            fileWriter.write(key + "," + value + " " + "\n");
            fileWriter.close();

            totalMergeFileBytesSize.addAndGet(totalBytesToBeWritten);
            int maxFileSize = Integer.parseInt((String) properties.get("max-file-size-in-mb"));


            if ((totalMergeFileBytesSize.get()/ (1024 * 1024)) >= maxFileSize) {
                try {
                    lock.lock();
                    fileRotationService.mergeRotation(targetFile);
                    totalMergeFileBytesSize.set(0L);
                } finally {
                    lock.unlock();
                }
            }
        } catch (IOException ignored) {
        }

        return new KeyValueMetadata(targetFile.getName(), keyByteOffset, keyByteLength, valueByteOffset, valueByteLength);

    }


}
