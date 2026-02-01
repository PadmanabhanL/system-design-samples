package com.app.service;

import com.app.common.StartupListener;
import com.app.index.bo.KeyValueMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.CRC32C;
import java.util.zip.Checksum;

public class FileWriterService {

    private final AtomicLong totalBytesSize;

    private final AtomicLong totalMergeFileBytesSize;

    private final Properties properties;

    private final FileRotationService fileRotationService;

    private final Lock lock = new ReentrantLock();

    private final Lock mergeLock = new ReentrantLock();

    public FileWriterService(FileRotationService fileRotationService) {
        this.totalBytesSize = new AtomicLong(0L);
        this.totalMergeFileBytesSize = new AtomicLong(0L);
        this.properties = StartupListener.getProperties();
        this.fileRotationService = fileRotationService;
    }


    public KeyValueMetadata saveAndRotate(String key, String value) {
        Checksum checksum = CrcFactory.create(CrcType.CRC32C);
        long timestamp = System.currentTimeMillis();

        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES)
                .order(ByteOrder.BIG_ENDIAN);
        buf.putLong(timestamp);

        checksum.update(buf.array());
        checksum.update(",".getBytes());
        checksum.update(key.getBytes());
        checksum.update(",".getBytes());
        checksum.update(value.getBytes());
        long checksumValue = checksum.getValue();

        try {
            lock.lock();

            int maxFileSize = Integer.parseInt((String) properties.get("max-file-size-in-mb"));
            if ((totalBytesSize.get()) / (1024 * 1024) >= maxFileSize) {
                fileRotationService.rotate(fileRotationService.getAppendOnlyFile());
                totalBytesSize.set(0L);
            }

            File targetFile = fileRotationService.getAppendOnlyFile();
            String checksumStr = String.valueOf(checksumValue);
            String timestampStr = String.valueOf(timestamp);

            long checksumStrLength = checksumStr.getBytes().length;
            long timestampStrLength = timestampStr.getBytes().length;

            long checkSumValueOffset = totalBytesSize.get();
            long timestampBytesOffset = checkSumValueOffset + checksumStrLength + ",".getBytes().length;
            long keyByteOffset = timestampBytesOffset + timestampStrLength + ",".getBytes().length;
            long keyByteLength = key.getBytes(StandardCharsets.UTF_8).length;
            long valueByteLength = value.getBytes(StandardCharsets.UTF_8).length;
            long valueByteOffset = keyByteOffset + keyByteLength + ",".getBytes().length;
            long totalBytesToBeWritten = checksumStrLength + ",".getBytes().length +
                    timestampStrLength + ",".getBytes().length +
                    keyByteLength + ",".getBytes().length +
                    valueByteLength + "\n".getBytes().length;
            try {
                writeKeyValueToFile(checksumValue, timestamp, key, value, targetFile);
            } catch (IOException ignored) {
            }
           /* long checkSumValueOffset = totalBytesSize.get() + 8 + ",".getBytes().length;
            long timestampBytesOffset = checkSumValueOffset + 8 + ",".getBytes().length; //8 bytes for long
            long keyByteOffset = timestampBytesOffset;
            long keyByteLength = key.getBytes().length;
            long valueByteOffset = timestampBytesOffset + keyByteLength + ",".getBytes().length;
            long valueByteLength = value.getBytes().length;
            long totalBytesToBeWritten = 8 + ",".getBytes().length +  8 + ",".getBytes().length + keyByteLength + ",".getBytes().length + valueByteLength + "\n".getBytes().length;*/
            totalBytesSize.addAndGet(totalBytesToBeWritten);
            return new KeyValueMetadata(targetFile.getName(), checkSumValueOffset, checksumStrLength, timestampBytesOffset, timestampStrLength,  keyByteOffset, keyByteLength, valueByteOffset, valueByteLength);
        } finally {

            lock.unlock();
        }
    }

    private void writeKeyValueToFile(long checkSumValue, long timestamp, String key, String value, File targetFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(targetFile, true)) {
            String line = checkSumValue + "," + timestamp + "," + key + "," + value + "\n";
            fos.write(line.getBytes(StandardCharsets.UTF_8));
        }
    }



    public KeyValueMetadata saveAndRotateForMerge(String key, String value) {

        Checksum checksum = CrcFactory.create(CrcType.CRC32C);
        long timestamp = System.currentTimeMillis();

        ByteBuffer buf = ByteBuffer.allocate(Long.BYTES)
                .order(ByteOrder.BIG_ENDIAN);
        buf.putLong(timestamp);

        checksum.update(buf.array());
        checksum.update(",".getBytes());
        checksum.update(key.getBytes());
        checksum.update(",".getBytes());
        checksum.update(value.getBytes());
        long checksumValue = checksum.getValue();

        try {
            mergeLock.lock();

            int maxFileSize = Integer.parseInt((String) properties.get("max-file-size-in-mb"));
            if ((totalMergeFileBytesSize.get()) / (1024 * 1024) >= maxFileSize) {
                fileRotationService.mergeRotation(fileRotationService.getMergeFile());
                totalMergeFileBytesSize.set(0L);
            }

            String checksumStr = String.valueOf(checksumValue);
            String timestampStr = String.valueOf(timestamp);

            long checksumStrLength = checksumStr.getBytes().length;
            long timestampStrLength = timestampStr.getBytes().length;

            long checkSumValueOffset = totalMergeFileBytesSize.get();
            long timestampBytesOffset = checkSumValueOffset + checksumStrLength + ",".getBytes().length;
            long keyByteOffset = timestampBytesOffset + timestampStrLength + ",".getBytes().length;
            long keyByteLength = key.getBytes(StandardCharsets.UTF_8).length;
            long valueByteLength = value.getBytes(StandardCharsets.UTF_8).length;
            long valueByteOffset = keyByteOffset + keyByteLength + ",".getBytes().length;

            long totalBytesToBeWritten = checksumStrLength + ",".getBytes().length +
                    timestampStrLength + ",".getBytes().length +
                    keyByteLength + ",".getBytes().length +
                    valueByteLength + "\n".getBytes().length;

            File targetFile = fileRotationService.getMergeFile();

            try {
                writeKeyValueToFile(checksumValue, timestamp, key, value, targetFile);
            } catch (IOException ignored) {
            }
            totalMergeFileBytesSize.addAndGet(totalBytesToBeWritten);
            return new KeyValueMetadata(targetFile.getName(), checkSumValueOffset, checksumStrLength, timestampBytesOffset, timestampStrLength,  keyByteOffset, keyByteLength, valueByteOffset, valueByteLength);
        } finally {
            mergeLock.unlock();
        }
    }


}
