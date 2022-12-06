package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Value
public class RestBook {
    private final static String UPLOAD_PATH = "/uploads/{id}";
    Long id;
    String title;
    Integer year;
    BigDecimal price;
    Long available;
    String bookCoverUrl;
    Set<RestBookAuthor> authors;

    public static List<RestBook> toRestBooks(List<Book> bookList, HttpServletRequest request) {
        return bookList.stream()
                .map(book -> new RestBook(
                        book.getId(),
                        book.getTitle(),
                        book.getYear(),
                        book.getPrice(),
                        book.getAvailable(),
                        toCoverUrl(book.getBookCoverId(), request),
                        toRestBookAuthor(book.getAuthors())))
                .collect(Collectors.toList());
    }

    private static Set<RestBookAuthor> toRestBookAuthor(Set<Author> authors) {
        return authors.stream().map(author -> {
            String name = StringUtils.isBlank(author.getName()) ? "" : author.getName();
            String lastName = StringUtils.isBlank(author.getLastName()) ? "" : author.getLastName();
            String fullName = String.join(" ", name, lastName);
            return new RestBookAuthor(fullName);
        }).collect(Collectors.toSet());
    }

    private static String toCoverUrl(Long id, HttpServletRequest request) {
        return Optional.ofNullable(id).map(coverId -> ServletUriComponentsBuilder
                        .fromContextPath(request)
                        .path(RestBook.UPLOAD_PATH)
                        .build(id)
                        .toASCIIString())
                .orElse(null);
    }
}
