package com.testaarosa.springRecallBookApp.catalog.application;

import com.testaarosa.springRecallBookApp.author.application.port.AuthorUseCase;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.catalog.application.port.*;
import com.testaarosa.springRecallBookApp.catalog.dataBase.BookJpaRepository;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.uploads.application.SaveUploadCommand;
import com.testaarosa.springRecallBookApp.uploads.application.UploadResponse;
import com.testaarosa.springRecallBookApp.uploads.application.port.UploadUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class CatalogService implements CatalogUseCase {
    private final BookJpaRepository bookJpaRepository;
    private final UploadUseCase uploadUseCase;
    private final AuthorUseCase authorUseCase;

    @Override
    public List<Book> findAll(Pageable pageable) {
        return bookJpaRepository.findAll(pageable).stream().toList();
    }

    @Override
    public List<Book> findAllEager(Pageable pageable) {
        return bookJpaRepository.findAllEager(pageable);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookJpaRepository.findById(id);
    }

    @Override
    public Book findOne(Long id) {
        return bookJpaRepository.getReferenceById(id);
    }

    @Override
    public List<Book> findByTitle(String title, Pageable pageable) {
        return bookJpaRepository.findAllByTitleContainsIgnoreCase(title, pageable);
    }

    @Override
    public List<Book> findByAuthor(String authorName, Pageable pageable) {
        return bookJpaRepository.findAllByLinkedAuthors_nameContainsIgnoreCaseOrLinkedAuthors_lastNameContainsIgnoreCase(authorName, authorName, pageable);
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author, Pageable pageable) {
        return bookJpaRepository.findAllByTitleAndAuthorName(title, author, pageable);
    }

    @Override
    @Transactional()
    public Book addBook(CreateBookCommand command) {
        Book bookToSave = toBook(command);
        return bookJpaRepository.save(bookToSave);
    }

    private Book toBook(CreateBookCommand command) {
        Book book = new Book(command.getTitle(),
                command.getYear(),
                command.getPrice(),
                command.getAvailAble());
        fetchAuthorsById(command.getAuthors()).forEach(book::addAuthor);
        return book;
    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return bookJpaRepository.findById(command.getId())
                .map(bookToUpdate -> {
                    Book updatedBook = updateBookFields(command, bookToUpdate);
                    //save method is not mandatory here when we use @Transactional annotation.
                    // Hibernate save it after finish all lines in the method
                    //but I prefer save method. It's clearer
                    bookJpaRepository.save(updatedBook);
                    return UpdateBookResponse.SUCCESS;
                }).orElseGet(() -> UpdateBookResponse.FAILURE(List.of("Book with ID: " + command.getId() + ", not found for update.")));
    }

    private Book updateBookFields(UpdateBookCommand command, Book book) {
        if (StringUtils.isNoneBlank(command.getTitle())) {
            book.setTitle(command.getTitle());
        }
        if (command.getAuthors() != null && command.getAuthors().size() > 0) {
            book.removeAuthors();
            fetchAuthorsById(command.getAuthors()).forEach(book::addAuthor);
        }
        if (command.getYear() != null && command.getYear() > 0) {
            book.setYear(command.getYear());
        }
        if (command.getPrice() != null && command.getPrice().compareTo(BigDecimal.ZERO) > -1) {
            book.setPrice(command.getPrice());
        }
        return book;
    }

    private Set<Author> fetchAuthorsById(Set<Long> authorsId) {
        return authorsId.stream()
                .map(authorId -> authorUseCase.findById(authorId)
                        .orElseThrow(() -> new IllegalArgumentException("No author found with ID: " + authorId)))
                .collect(Collectors.toSet());
    }


    @Override
    public void removeById(Long id) {
        bookJpaRepository.deleteById(id);
    }

    @Override
    public void updateBookCover(UpdateBookCoverCommand command) {
        log.info("Received cover command: " + command.getFileName()
                + ", bytes: " + command.getFile().length);
        bookJpaRepository.findById(command.getId())
                .ifPresent(book -> {
                    log.info("Book, id: " + book.getId() + " has been found");
                    UploadResponse uploadResponse = uploadUseCase.save(SaveUploadCommand.builder()
                            .fileName(command.getFileName())
                            .file(command.getFile())
                            .contentType(command.getFileContentType())
                            .build());
                    book.setBookCoverId(uploadResponse.getId());
                    Book savedBook = bookJpaRepository.save(book);
                    log.info("Book id " + savedBook.getId() + " has been updated. Cover path added: " + uploadResponse.getPath());
                });
    }

    @Override
    public void removeCoverByBookId(Long id) {
        log.info("Trying to delete book cover");
        bookJpaRepository.findById(id).ifPresent(book -> {
            if (book.getBookCoverId() != null) {
                uploadUseCase.removeCoverById(book.getBookCoverId());
                book.setBookCoverId(null);
                bookJpaRepository.save(book);
            }
        });
    }

    @Override
    public void saveAll(Set<Book> updateBooksQuantity) {
        bookJpaRepository.saveAll(updateBooksQuantity);
    }

}
