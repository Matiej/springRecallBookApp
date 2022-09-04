package com.testaarosa.spirngRecallBookApp.uploads.domain;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class Upload {
    String id;
    String fileName;
    String content;
    byte[] file;
    LocalDateTime createdAt;
}
