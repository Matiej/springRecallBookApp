package com.testaarosa.springRecallBookApp.catalog.application.port;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@Value
public class CreateBookCommand {
    String title;
    Set<Long> authors;
    Integer year;
    BigDecimal price;
}
