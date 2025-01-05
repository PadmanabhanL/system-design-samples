package com.app.service;


import java.util.Timer;
import java.util.TimerTask;

public class MergeAndCompactionService {

    private final FileRotationService fileRotationService;

    private final IndexService indexService;

    public MergeAndCompactionService(FileRotationService fileRotationService, IndexService indexService) {
        this.fileRotationService = fileRotationService;
        this.indexService = indexService;
    }

    public void mergeAndCompact() {
        Timer timer = new Timer();
        TimerTask mergeTask = new MergeAndCompactionTask(fileRotationService, indexService);
        timer.schedule(mergeTask, 10000, 1);
    }
}
