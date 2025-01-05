package com.app.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StartupListener {

    private static Properties properties;

    private static String fileName;

    private static int counter = 1;

    private static File appendOnlyFile;

    private static String filePath;

    private static List<File> fileList;

    static {
        InputStream resourceAsStream = StartupListener.class.getClassLoader().getResourceAsStream("app.properties");

        properties = new Properties();
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            System.out.println("Unable to load properties file");
            throw new RuntimeException(e);
        }
        String filePrefix = (String) properties.get("file-name-prefix");
        filePath = properties.getProperty("storage-path");
        appendOnlyFile = new File(filePath + filePrefix + counter + ".log");
        fileList = new ArrayList<>();
    }

    public static Properties getProperties() {
        return properties;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        StartupListener.fileName = fileName;
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        StartupListener.counter = counter;
    }

    public static void rotateFile() {
        counter = counter + 1;
        String filePrefix = (String) properties.get("file-name-prefix");
        filePath = properties.getProperty("storage-path");
        appendOnlyFile = new File(filePath + filePrefix + counter + ".log");
    }

    public static File getAppendOnlyFile() {
        return appendOnlyFile;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void addFile(File file) {
        fileList.add(file);
    }

    public static List<File> getFileList() {
        return fileList;
    }
}
