package com.testaarosa.springRecallBookApp.catalog.application;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Set;

@Value
@Builder(builderMethodName = "hiddenBuilder")
public class UpdateBookCommand {
    Long id;
    String title;
    Set<Long> authors;
    Integer year;
    BigDecimal price;
    Long available;
    public static UpdateBookCommandBuilder builder(Long id) {
        return hiddenBuilder().id(id);
    }
}
