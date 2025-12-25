package com.app.service;

import com.app.common.StartupListener;
import com.app.index.bo.KeyValueMetadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FileRotationService {

    private final AtomicLong totalBytesSize;

    private final AtomicLong totalMergeFileBytesSize;

    private final AtomicInteger counter = new AtomicInteger(1);

    private File appendOnlyFile;

    private final String filePath;

    private final String filePrefix;

    private final Set<File> files;

    private File mergeFile;

    public FileRotationService() {
        this.totalBytesSize = new AtomicLong(0);
        this.totalMergeFileBytesSize = new AtomicLong(0);
        Properties properties = StartupListener.getProperties();
        this.filePrefix = (String) properties.get("file-name-prefix");
        this.filePath = properties.getProperty("storage-path");
        this.appendOnlyFile = new File(filePath + filePrefix + counter + ".log");
        this.mergeFile = new File(filePath + filePrefix + "_merge_"+counter + ".log");
        this.files = new LinkedHashSet<>();
    }

    public void rotate(File currentFile) {
        counter.addAndGet(1);
        this.getFiles().add(currentFile);
        this.appendOnlyFile = new File(getFilePath() + getFilePrefix() +  counter.get() + ".log");
        this.totalBytesSize.set(0);
    }

    public void mergeRotation(File currentFile) {
        counter.addAndGet(1);
        this.getFiles().add(currentFile);
        this.mergeFile = new File(getFilePath() + getFilePrefix()  + "_merge_"+  counter.get() + ".log");
        this.totalMergeFileBytesSize.set(0);
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

    public String getFilePrefix() {
        return filePrefix;
    }

    public File getAppendOnlyFile() {
        return appendOnlyFile;
    }
}
