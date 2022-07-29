package com.testaarosa.spirngRecallBookApp.catalog.application.port;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class CreateBookCommand {
    String title;
    String author;
    Integer year;
    BigDecimal price;
}
