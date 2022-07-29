package com.testaarosa.spirngRecallBookApp.catalog.application.port;

import lombok.Value;

@Value
public class UpdateBookCommand {
    Long id;
    String title;
    String author;
    Integer year;
}
