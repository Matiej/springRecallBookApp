package com.testaarosa.springRecallBookApp.uploads.infrastructure;

import com.testaarosa.springRecallBookApp.uploads.application.SaveUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.UploadResponse;
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
class ServerUploadRepositoryImpl implements ServerUploadRepository {
    @Value("${cover.picture.path}")
    private String UPLOAD_PATH;
    @Value("${cover.user.app.dir}")
    private String USER_DIR;

    public UploadResponse save(SaveUploadCommand command) {
        Path coverPath = createCoverDirectory();
        String uploadFileAbsolutePath = new StringJoiner(File.separator)
                .add(coverPath.toString())
                .add(createFileNameToSave(command.getFileName()))
                .toString();
        File coverFile = new File(uploadFileAbsolutePath);
        try (FileOutputStream os = new FileOutputStream(coverFile)) {
            os.write(command.getFile());
            log.info("File has been saved: " + uploadFileAbsolutePath);
        } catch (IOException e) {
            log.error("Can't save file to path: " + uploadFileAbsolutePath);
        }
        return UploadResponse.builder()
                .originFileName(command.getFileName())
                .serverFileName(coverFile.getName())
                .contentType(command.getContentType())
                .file(command.getFile())
                .createdAt(LocalDateTime.now())
                .path(coverFile.getAbsolutePath())
                .build();
    }

    private Path createCoverDirectory() {
        String coversPath = getUplaodsPath()
                .toString();
        Path pathToCreate = Paths.get(coversPath);
        if (!Files.exists(pathToCreate)) {
            log.info("There is no covers directory " + pathToCreate);
            try {
                Files.createDirectories(pathToCreate);
                log.info("Upload file directory has been created=> " + coversPath);
            } catch (IOException e) {
                String errorMessage = "Can't create upload directory=> " + coversPath;
                log.error(errorMessage);
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

    public byte[] getFileByPath(String path) {
        Path coverPath = Path.of(path);
        byte[] file = null;
        if (coverPath.isAbsolute()) {
            try {
                file = Files.readAllBytes(coverPath);
            } catch (IOException e) {
                log.error("File not found in path: " + path);
            }

        }
        return file;
    }

    private StringJoiner getUplaodsPath() {
        return new StringJoiner(File.separator)
                .add(System.getProperty(USER_DIR))
                .add(UPLOAD_PATH);
    }

    public void removeFileByPath(String path) {
        Path coverPath = Path.of(path);
        if (coverPath.isAbsolute()) {
            try {
                Files.delete(coverPath);
                log.info("File has been deleted: " + coverPath.getFileName());
            } catch (IOException e) {
                String errorMessage = "Can't delete file in path: " + path;
                log.error(errorMessage);
            }
        }
    }
}
