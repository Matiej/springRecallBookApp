package com.testaarosa.springRecallBookApp.catalog.application.port;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateBookCoverCommand {
    Long id;
    byte[] file;
    String fileName;
    String fileContentType;

}