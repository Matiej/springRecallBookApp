package com.testaarosa.springRecallBookApp.catalog.application.port;

import com.testaarosa.springRecallBookApp.catalog.application.CreateBookCommand;
import com.testaarosa.springRecallBookApp.catalog.application.UpdateBookCommand;
import com.testaarosa.springRecallBookApp.catalog.application.UpdateBookCoverCommand;
import com.testaarosa.springRecallBookApp.catalog.application.UpdateBookResponse;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CatalogUseCase {

    List<Book> findAll(Pageable pageable);

    List<Book> findAllEager(Pageable pageable);

    Optional<Book> findById(Long id);
    Book findOne(Long id);

    List<Book> findByTitle(String title, Pageable pageable);

    List<Book> findByAuthor(String author, Pageable pageable);

    List<Book> findByTitleAndAuthor(String title, String author, Pageable pageable);

    Book addBook(CreateBookCommand command);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    void removeById(Long id);

    void updateBookCover(UpdateBookCoverCommand command);

    void removeCoverByBookId(Long id);

    void saveAll(Set<Book> updateBooksQuantity);
}
