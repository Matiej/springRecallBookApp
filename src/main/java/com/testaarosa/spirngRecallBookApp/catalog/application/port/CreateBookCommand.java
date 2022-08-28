package com.testaarosa.spirngRecallBookApp.catalog.application.port;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookCommand {
    String title;
    String author;
    Integer year;
    BigDecimal price;
}
