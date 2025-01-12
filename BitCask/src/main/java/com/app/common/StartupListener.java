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

}
