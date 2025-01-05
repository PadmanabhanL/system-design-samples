package com.app.service;

import com.app.common.StartupListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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

    public Map<String, Object> saveAndRotate(String key, String value, boolean mergeAndCompactFlag) {

        File targetFile = appendOnlyFile;

        if (mergeAndCompactFlag) {
            counter = (counter * 10) + 1;
            if (mergeFile == null) {
                mergeFile = new File(filePath + filePrefix + counter + ".log");
            }
            targetFile = mergeFile;
        }
        String tempFileName = targetFile.getName();

        int currentByteOffset = totalBytesSize;

        int currentByteLength = 0;

        try {
            FileWriter fileWriter = new FileWriter(targetFile, true);
            String keyValue = key + "," + value + "\n";
            currentByteLength += keyValue.getBytes().length;
            totalBytesSize += currentByteLength;
            fileWriter.write(keyValue);
            fileWriter.close();

            int maxFileSize = Integer.parseInt((String) properties.get("max-file-size-in-mb"));
            if ((totalBytesSize / (1024 * 1)) >= maxFileSize) {
                counter = counter + 1;
                files.add(targetFile);
                targetFile = new File(filePath + filePrefix + counter + ".log");
                appendOnlyFile = targetFile;
                totalBytesSize = 0;
            }

        } catch (IOException ignored) {
        }

        return Map.of("fileName", tempFileName,"byteOffset",
                      currentByteOffset, "byteLength", currentByteLength);

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
