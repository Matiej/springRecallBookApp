package com.testaarosa.springRecallBookApp.uploads.controller;

import com.testaarosa.springRecallBookApp.globalHeaderFactory.HeaderKey;
import com.testaarosa.springRecallBookApp.uploads.application.UpdateUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.UpdateUploadResponse;
import com.testaarosa.springRecallBookApp.uploads.application.port.UploadUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;

@Slf4j
@RestController
@RequestMapping("/uploads")
@Validated
@RequiredArgsConstructor
@Tag(name = "Uploads API", description = "API designed to manipulate the upload object and file")
@SecurityRequirement(name = "springrecallbook-api_documentation")
class UploadsController {
    private final UploadUseCase uploadUseCase;


    @GetMapping(value = "/{id}",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE,
                    MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE},
            consumes = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE,
                    MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @Operation(summary = "Get book cover by ID", description = "Get book cover by cover ID")
    @Parameter(name = "id", required = true, description = "Searching cover ID")
    @Schema(example = "picture file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No cover found!"),
    })
    ResponseEntity<?> getUploadCoverById(@PathVariable("id") @NotNull(message = "CoverId filed can't be empty or null")
                                         @Min(value = 1, message = "CoverId field must be greater then 0") Long id) {
        return uploadUseCase.getCoverUploadById(id)
                .map(file -> ResponseEntity
                        .ok()
                        .contentType(MediaType.parseMediaType(file.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName:\"" + file.getOriginFileName() + "\"")
                        .header(HeaderKey.SERVER_FILENAME.getHeaderKeyLabel(), file.getServerFileName())
                        .header(HeaderKey.CREATED_AT.getHeaderKeyLabel(), file.getCreatedAt().toString())
                        .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                        .body(new ByteArrayResource(file.getFile())))
                .orElseGet(() -> {
                    String message = "File with ID: '" + id + "' not found";
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NOT_FOUND.name())
                            .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), message)
                            .build();
                });
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Update upload by id", description = "Update upload by id")
    @Parameter(name = "id", required = true, description = "Uploaded cover id, mandatory")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Upload updated successful"),
            @ApiResponse(responseCode = "204", description = "Can't update, no upload - book cover found"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
    })
    ResponseEntity<?> updateUpload(@PathVariable("id") @NotNull(message = "UploadId field can't be empty or null")
                                   @Min(value = 1, message = "UploadId field can't be lower then 1") Long id,
                                   @RequestParam(value = "cover") MultipartFile cover) throws IOException {
        log.info("Received request with file: " + cover.getOriginalFilename());
        UpdateUploadResponse uploadResponse = uploadUseCase.updateById(new UpdateUploadCommand(id, cover.getOriginalFilename(),
                cover.getBytes(), cover.getContentType()));
        if (!uploadResponse.isSuccess()) {
            return ResponseEntity.noContent()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.PATCH.name())
                    .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NO_CONTENT.name())
                    .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), uploadResponse.getErrorList().toString())
                    .build();
        }
        URI uri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/uploads")
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.accepted()
                .location(uri)
                .headers(getSuccessfulHeaders(HttpStatus.ACCEPTED, HttpMethod.PATCH))
                .header(HeaderKey.SERVER_FILENAME.getHeaderKeyLabel(), uploadResponse.getServerFileName())
                .header(HeaderKey.CREATED_AT.getHeaderKeyLabel(), uploadResponse.getCreatedAt().toString())
                .header(HeaderKey.UPDATED_AT.getHeaderKeyLabel(), uploadResponse.getLastUpdateAt().toString())
                .build();
    }

}
