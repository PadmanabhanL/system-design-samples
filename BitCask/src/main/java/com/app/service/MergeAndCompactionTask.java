package com.app.service;

import com.app.common.StartupListener;
import com.app.index.bo.ValueMetadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MergeAndCompactionTask extends TimerTask {

    private final FileRotationService fileRotationService;

    private final IndexService indexService;

    private final Set<File> filesToBeRemoved;

    public MergeAndCompactionTask(FileRotationService fileRotationService, IndexService indexService) {
        this.fileRotationService = fileRotationService;
        this.indexService = indexService;
        this.filesToBeRemoved = new HashSet<>();
    }

    @Override
    public void run() {

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
                    String key = kvPairArr[0];
                    if (!indexService .getIndex().containsKey(key)) {
                        continue; //SKIP This data as this item may have been deleted
                    }
                    String valueFromIndex = indexService.findValueByKey(key);
                    //FIXME Needs to fix the logic while using RandomAccessFile
                    valueFromIndex = valueFromIndex.replace("\n", "");
                    if (kvPairArr[1].equals(valueFromIndex)) {
                        Map<String, Object> map = fileRotationService.saveAndRotate(key, kvPairArr[1], true);
                        indexService.getIndex().put(key, new ValueMetadata((String) map.get("fileName"),
                                                                           (int) map.get("byteOffset"),
                                                                           (int) map.get("byteLength")));
                    }
                }
                filesToBeRemoved.add(file);
            } catch (FileNotFoundException e) {
            }
        }

        /*for (File file: filesToBeRemoved) {
            file.renameTo(new File(""));
        }*/

    }
}
