package com.testaarosa.spirngRecallBookApp.catalog.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class Book {
    private Long id;
    private String title;
    private String author;
    private Integer year;
    private BigDecimal price;
    private String bookCoverId;
    //temporary until will not save in sql data base
    //todo remove bookCoverPath from book when created data base table for it
    private String bookCoverPath;

    public Book(String title, String author, Integer year, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
    }
}
