package com.testaarosa.spirngRecallBookApp.uploads.controller;

import com.testaarosa.spirngRecallBookApp.uploads.application.port.UploadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.testaarosa.spirngRecallBookApp.catalog.controller.HttpHeaderFactory.getSuccessfulHeaders;

@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class UploadsController {
    private final UploadUseCase uploadUseCase;


    @GetMapping(value = "/{id}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE,
            "application/json", "application/octet-stream"})
    public ResponseEntity<?> getUploadCoverById(@PathVariable String id) {
        return uploadUseCase.getCoverUploadById(id)
                .map(file -> ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType(file.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName:\"" + file.getOriginFileName() + "\"")
                        .header("ServerFileName", file.getServerFileName())
                        .header("CreatedAt", file.getCreatedAt().toString())
                        .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                        .body(new ByteArrayResource(file.getFile())))
                .orElseGet(() -> {
                    String message = "File with ID: '" + id + "' not found";
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Status", HttpStatus.NOT_FOUND.name())
                            .header("Message", message)
                            .build();
                });
    }
}
