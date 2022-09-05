package com.testaarosa.spirngRecallBookApp.uploads.infrastructure;

import com.testaarosa.spirngRecallBookApp.uploads.domain.Upload;
import com.testaarosa.spirngRecallBookApp.uploads.domain.UploadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.StringJoiner;

@Slf4j
@Repository
public class ServerUploadRepository implements UploadRepository {
    @Value("${cover.picture.path}")
    private String UPLOAD_PATH;
    @Value("${cover.user.app.dir}")
    private String USER_DIR;

    @Override
    public Upload saveUploadOnServer(Upload upload) {
        Path coverPath = createCoverPath();
        String uploadFileAbsolutePath = new StringJoiner(File.separator)
                .add(coverPath.toString())
                .add(createFileNameToSave(upload.getFileName()))
                .toString();
        File coverFile = new File(uploadFileAbsolutePath);
        try (FileOutputStream os = new FileOutputStream(coverFile)) {
            os.write(upload.getFile());
            log.info("File has been saved: " + upload.getPath());
        } catch (IOException e) {
            log.error("Can't upload file to path: " + uploadFileAbsolutePath);
        }
        upload.setPath(coverFile.getPath());
        upload.setId(coverFile.getName());
        return upload;
    }

    private Path createCoverPath() {
        String coversPath = new StringJoiner(File.separator)
                .add(System.getProperty(USER_DIR))
                .add(UPLOAD_PATH)
                .toString();
        Path pathToCreate = Paths.get(coversPath);
        if (!Files.exists(pathToCreate)) {
            log.info("There is no covers directory " + pathToCreate);
            try {
                Files.createDirectories(pathToCreate);
                log.info("Upload file directory has been created=> " + coversPath);
            } catch (IOException e) {
                log.error("Can't create upload directory=> " + coversPath);
            }
        }
        return pathToCreate;
    }

    private String createFileNameToSave(String uploadedFileName) {
        return new StringJoiner("_")
                .add(LocalDateTime.now().withNano(0).toString().replaceAll(":", "-"))
                .add(uploadedFileName)
                .toString();
    }
}
