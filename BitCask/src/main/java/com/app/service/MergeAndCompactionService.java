package com.app.service;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MergeAndCompactionService {

    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final FileRotationService fileRotationService;
    private final IndexService indexService;
    private final FileWriterService fileWriterService;

    public MergeAndCompactionService(FileRotationService fileRotationService, IndexService indexService, FileWriterService fileWriterService) {
        this.fileRotationService = fileRotationService;
        this.indexService = indexService;
        this.fileWriterService = fileWriterService;
    }

    public void mergeAndCompact() {

        MergeAndCompactionTask mergeAndCompactionTask = new MergeAndCompactionTask(this.fileRotationService, this.fileWriterService, this.indexService);
        executor.scheduleAtFixedRate(mergeAndCompactionTask, 10, 10, TimeUnit.SECONDS);
    }
}
