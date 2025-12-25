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

    public MergeAndCompactionTask(FileRotationService fileRotationService, FileWriterService fileWriterService, IndexService indexService) {
        this.fileRotationService = fileRotationService;
        this.fileWriterService = fileWriterService;
        this.indexService = indexService;
        this.filesToBeRemoved = new HashSet<>();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        Set<File> fileList = new HashSet<>(fileRotationService.getFiles());
        for (File file: fileList) {
            if (filesToBeRemoved.contains(file)
                || fileRotationService.getMergeFile() != null && fileRotationService.getMergeFile().getName().equals(file.getName())) {
                System.out.println("Continuing...");
                continue;
            }
            try {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String kvPair = scanner.nextLine();
                    String[] kvPairArr = kvPair.split("\\,");
                    String key = kvPairArr[0];
                    if (!indexService.getIndex().containsKey(key)) {
                        System.out.println("Item deleted");
                        continue; //SKIP This data as this item may have been deleted
                    }
                    String valueFromIndex = indexService.findValueByKey(key);
                    //FIXME Needs to fix the logic while using RandomAccessFile
                   // valueFromIndex = valueFromIndex.replace("\n", "");
                    if (kvPairArr[1].equals(valueFromIndex)) {
                        System.out.println("calling from merge task");
                        KeyValueMetadata kvMetadata = fileWriterService.saveAndRotateForMerge(key, kvPairArr[1], fileRotationService.getMergeFile());
                        indexService.getIndex().put(key, kvMetadata);
                    } else {
                       // System.out.println("Value not matching " + kvPairArr[1] + " " + valueFromIndex + indexService.getIndex().get(key));
                    }
                }
                System.out.println("adding file to be removed");
                filesToBeRemoved.add(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Time taken to Compact and merge "+ filesToBeRemoved.size() + " "  + (fileRotationService.getMergeFile() != null ? fileRotationService.getMergeFile().getName() : "NULL Merge file")+ " " + (endTime - startTime) + "ms");
    }
}
