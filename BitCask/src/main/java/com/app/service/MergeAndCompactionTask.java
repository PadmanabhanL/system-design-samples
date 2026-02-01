package com.app.service;

import com.app.index.bo.KeyValueMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MergeAndCompactionTask extends TimerTask {

    private final FileRotationService fileRotationService;

    private final FileWriterService fileWriterService;

    private final IndexService indexService;

    private final Set<File> filesToBeRemoved;

    private final Set<String> processedKeys;

    public MergeAndCompactionTask(FileRotationService fileRotationService, FileWriterService fileWriterService, IndexService indexService) {
        this.fileRotationService = fileRotationService;
        this.fileWriterService = fileWriterService;
        this.indexService = indexService;
        this.filesToBeRemoved = new HashSet<>();
        this.processedKeys = new HashSet<>();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        Set<File> fileList = new HashSet<>(fileRotationService.getFiles());
        for (File file: fileList) {
            if (filesToBeRemoved.contains(file)
                || fileRotationService.getMergeFile() != null && fileRotationService.getMergeFile().getName().equals(file.getName())) {
                continue;
            }
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String kvPair = scanner.nextLine();
                    String[] kvPairArr = kvPair.split("\\,");
                    String key = kvPairArr[2];      // Correct index for key
                    
                    // Skip if already processed in this compaction cycle or if key was deleted
                    if (processedKeys.contains(key) || !indexService.getIndex().containsKey(key)) {
                        continue;
                    }
                    
                    // Get the CURRENT value from index (not from file) to avoid race conditions
                    String currentValue = indexService.findValueByKey(key);
                    
                    // Write to merge file and update index
                    KeyValueMetadata kvMetadata = fileWriterService.saveAndRotateForMerge(key, currentValue);
                    indexService.getIndex().put(key, kvMetadata);
                    processedKeys.add(key);
                }
                scanner.close();
                filesToBeRemoved.add(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Time taken to Compact and merge "+ filesToBeRemoved.size() + " files, processed " + processedKeys.size() + " keys. "  + (fileRotationService.getMergeFile() != null ? fileRotationService.getMergeFile().getName() : "NULL Merge file")+ " " + (endTime - startTime) + "ms");
    }
}
