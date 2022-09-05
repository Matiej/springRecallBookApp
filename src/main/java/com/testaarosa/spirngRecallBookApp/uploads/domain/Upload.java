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
    private String content;
    private byte[] file;
    private LocalDateTime createdAt;
    @Setter
    private String path;

}
