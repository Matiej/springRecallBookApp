package com.testaarosa.springRecallBookApp.catalog.application;

import com.testaarosa.springRecallBookApp.catalog.application.port.*;
import com.testaarosa.springRecallBookApp.catalog.dataBase.BookJpaRepository;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.uploads.application.port.SaveUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.port.UploadResponse;
import com.testaarosa.springRecallBookApp.uploads.application.port.UploadUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
class CatalogService implements CatalogUseCase {
    //    private final CatalogRepository repository;
    private final BookJpaRepository repository;
    private final UploadUseCase uploadUseCase;

    // qualifier left to remember this kind of solution, if. Maybe will delete later
    public CatalogService(BookJpaRepository repository,
                          UploadUseCase uploadUseCase) {
        this.repository = repository;
        this.uploadUseCase = uploadUseCase;
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return repository.findAll()
                .stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return repository.findAll()
                .stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        return repository.findAll()
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
        return repository.findAll()
                .stream()
                .filter(bookTitle -> bookTitle.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(bookAuthor -> bookAuthor.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .findAny();
    }

    @Override
    public Book addBook(CreateBookCommand command) {
        return repository.save(new Book(command.getTitle(), command.getAuthor(), command.getYear(), command.getPrice()));
    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return repository.findById(command.getId())
                .map(bookToUpdate -> {
                    Book updatedBook = command.updateBookFields(bookToUpdate);
                    repository.save(updatedBook);
                    return UpdateBookResponse.SUCCESS;
                }).orElseGet(() -> UpdateBookResponse.FAILURE(List.of("No book for update found for ID: " + command.getId())));
    }

    @Override
    public void removeById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        log.info("Received cover command: " + command.getFileName()
                + ", bytes: " + command.getFile().length);
        repository.findById(command.getId())
                .ifPresent(book -> {
                    log.info("Book, id: " + book.getId() + " has been found");
                    UploadResponse uploadResponse = uploadUseCase.save(SaveUploadCommand.builder()
                            .fileName(command.getFileName())
                            .file(command.getFile())
                            .contentType(command.getFileContentType())
                            .build());
                    book.setBookCoverId(uploadResponse.getId());
                    Book savedBook = repository.save(book);
                    log.info("Book id " + savedBook.getId() + " has been updated. Cover path added: " + uploadResponse.getPath());
                });
    }

    @Override
    public void removeCoverByBookId(Long id) {
        log.info("Trying to delete book cover");
        repository.findById(id).ifPresent(book -> {
            if (book.getBookCoverId() != null) {
                uploadUseCase.removeCoverById(book.getBookCoverId());
                book.setBookCoverId(null);
                repository.save(book);
            }
        });
    }

}
