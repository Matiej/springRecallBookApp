package com.testaarosa.spirngRecallBookApp.catalog.application;

import com.testaarosa.spirngRecallBookApp.catalog.application.port.*;
import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import com.testaarosa.spirngRecallBookApp.catalog.domain.CatalogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
class CatalogService implements CatalogUseCase {
    private final CatalogRepository catalogRepository;

    // qualifier left to remember this kind of solution, if. Maybe will delete later
    public CatalogService(@Qualifier("memoryCatalogRepository") CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Override
    public List<Book> findAll() {
        return catalogRepository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return catalogRepository.findById(id);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findOneByTitle(String title) {
        return findAll()
                .stream()
                .filter(book -> book.getTitle().equals(title))
                .findAny();
    }

    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return catalogRepository.findAll()
                .stream()
                .filter(bookTitle -> bookTitle.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(bookAuthor -> bookAuthor.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .findAny();
    }

    @Override
    public Book addBook(CreateBookCommand command) {
        return catalogRepository.save(new Book(command.getTitle(), command.getAuthor(), command.getYear(), command.getPrice()));
    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return catalogRepository.findById(command.getId())
                .map(bookToUpdate -> {
                    Book updatedBook = command.updateBookFields(bookToUpdate);
                    catalogRepository.save(updatedBook);
                    return UpdateBookResponse.SUCCESS;
                }).orElseGet(() -> errorResponse(command.getId()));
    }

    private UpdateBookResponse errorResponse(Long id) {
        List<String> errorList = new ArrayList<>();
        errorList.add("No book for update found for ID: " + id);
        return new UpdateBookResponse(false, errorList);
    }


    @Override
    public void removeById(Long id) {
        catalogRepository.removeBookById(id);
    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        log.info("Received cover command: " + command.getFileName()
                + ", bytes: " + command.getFile().length);
        catalogRepository.findById(command.getId())
                .ifPresent(book -> {
                    log.info("Book, id: " + book.getId() + " has been found");

                });
    }

}
