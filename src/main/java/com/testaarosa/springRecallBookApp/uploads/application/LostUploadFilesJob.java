package com.testaarosa.springRecallBookApp.uploads.application;

import com.testaarosa.springRecallBookApp.uploads.application.port.UploadUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
class LostUploadFilesJob {
    private final UploadUseCase uploadUseCase;
    private final UploadRepository uploadRepository;

    @Transactional
    @Scheduled(cron = "${upload.lost.files.cron}")
    public void run() {
        log.info("Start job: " + this.getClass().getSimpleName() + " to find deleted cover pictures with thumbnails");
        List<UploadResponse> allUploadsWithFileUri = uploadRepository.getAllUploadsWithFileUri();
        log.info("Find " + allUploadsWithFileUri.size() + ", uploads with uri.");
        long count = allUploadsWithFileUri.stream()
                .filter(p -> null == p.getFile())
                .map(uploadResponse -> {
                    UploadResponse renewUploadOnServer = uploadUseCase.renewUploadOnServer(uploadResponse.getId());
                    log.info("File: " + renewUploadOnServer.getOriginFileName() + " was downloaded and renewed on the server.");
                    return uploadResponse;
                })
                .count();
        log.info("Total files renewed: " + count);
        log.info("End of job: " + this.getClass().getSimpleName());
    }
}
