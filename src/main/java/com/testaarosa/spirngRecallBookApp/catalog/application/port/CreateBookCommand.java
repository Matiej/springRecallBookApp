package com.testaarosa.spirngRecallBookApp.catalog.application.port;

import lombok.Value;

@Value
public class CreateBookCommand {
    String title;
    String author;
    Integer year;
}
