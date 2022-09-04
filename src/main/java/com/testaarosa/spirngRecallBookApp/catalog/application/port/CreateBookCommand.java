package com.testaarosa.spirngRecallBookApp.catalog.application.port;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Builder
@Value
public class CreateBookCommand {
    String title;
    String author;
    Integer year;
    BigDecimal price;
}
