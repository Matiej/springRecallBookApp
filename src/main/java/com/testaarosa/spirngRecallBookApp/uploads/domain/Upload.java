package com.testaarosa.spirngRecallBookApp.uploads.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder()
@Getter
public class Upload {
    @Setter
    private String id;
    private String fileName;
    private String serverFileName;
    private String contentType;
    private LocalDateTime createdAt;
    private String path;

}
