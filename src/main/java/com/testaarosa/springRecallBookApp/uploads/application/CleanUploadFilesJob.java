package com.testaarosa.springRecallBookApp.uploads.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
class CleanUploadFilesJob {
    private final UploadRepository uploadRepository;

    @Transactional
    @Scheduled(cron = "${upload.clean.files.cron}")
    public void run() {
        log.info("Start job: " + this.getClass().getSimpleName() + " clean useless cover files");
        uploadRepository.cleanUselessCoverFiles();
        log.info("End of job: " + this.getClass().getSimpleName());
    }
}
