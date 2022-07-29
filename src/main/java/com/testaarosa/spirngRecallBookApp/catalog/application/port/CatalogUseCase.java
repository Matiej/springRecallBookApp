package com.testaarosa.spirngRecallBookApp.catalog.application.port;

import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;

import java.util.List;
import java.util.Optional;

public interface CatalogUseCase {

    List<Book> findAll();

    List<Book> findByTitle(String title);

    Optional<Book> findOneByTitleAndAuthor(String title, String author);

    void addBook(CreateBookCommand command);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    void removeById(Long id);
}
