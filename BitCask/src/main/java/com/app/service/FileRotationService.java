package com.app.service;

import com.app.common.StartupListener;
import com.app.index.bo.KeyValueMetadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

public class FileRotationService {

    private int totalBytesSize;

    private static int counter = 1;

    private File appendOnlyFile;

    private final Properties properties;

    private final String filePath;

    private String filePrefix;

    private Set<File> files;

    private File mergeFile;

    public FileRotationService() {
        this.totalBytesSize = 0;
        this.properties = StartupListener.getProperties();
        this.filePrefix = (String) properties.get("file-name-prefix");
        this.filePath = properties.getProperty("storage-path");
        this.appendOnlyFile = new File(filePath + filePrefix + counter + ".log");
        this.files = new LinkedHashSet<>();
    }

    public KeyValueMetadata saveAndRotate(String key, String value, boolean mergeAndCompactFlag) {

        File targetFile = appendOnlyFile;

        if (mergeAndCompactFlag) {
            if (mergeFile == null) {
                counter = (counter * 10) + 1;
                mergeFile = new File(filePath + filePrefix + counter + ".log");
                totalBytesSize = 0;
            }
            targetFile = mergeFile;
        }
        String tempFileName = targetFile.getName();

        int keyByteOffset = totalBytesSize;

        int keyByteLength = 0;

        int valueByteOffset = 0;

        int valueByteLength = 0;

        try {
            FileWriter fileWriter = new FileWriter(targetFile, true);
            keyByteLength += key.getBytes().length;

            totalBytesSize += keyByteLength;

            totalBytesSize += ",".getBytes().length;

            valueByteOffset = totalBytesSize;

            valueByteLength = value.getBytes().length;

            totalBytesSize += valueByteLength;

            totalBytesSize += "\n".getBytes().length;

            fileWriter.write(key + "," + value + "\n");
            fileWriter.close();

            int maxFileSize = Integer.parseInt((String) properties.get("max-file-size-in-mb"));
            if ((totalBytesSize / (1024 * 1024)) >= maxFileSize) {
                counter = counter + 1;
                files.add(targetFile);
                targetFile = new File(filePath + filePrefix + counter + ".log");
                if (mergeAndCompactFlag) {
                    mergeFile = targetFile;
                } else {
                    appendOnlyFile = targetFile;
                }
                totalBytesSize = 0;
            }

        } catch (IOException ignored) {
        }

        return new KeyValueMetadata(tempFileName, keyByteOffset, keyByteLength, valueByteOffset, valueByteLength);

    }

    public String getFilePath() {
        return filePath;
    }

    public Set<File> getFiles() {
        return files;
    }

    public File getMergeFile() {
        return mergeFile;
    }
}
