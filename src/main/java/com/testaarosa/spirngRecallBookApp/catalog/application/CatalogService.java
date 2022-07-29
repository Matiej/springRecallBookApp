package com.testaarosa.spirngRecallBookApp.catalog.application;

import com.testaarosa.spirngRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.CreateBookCommand;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.UpdateBookCommand;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.UpdateBookResponse;
import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import com.testaarosa.spirngRecallBookApp.catalog.domain.CatalogRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class CatalogService implements CatalogUseCase {
    private final CatalogRepository catalogRepository;

    // qualifier left to remember this kind of solution, if. Maybe will delete later
    public CatalogService(@Qualifier("memoryCatalogRepository") CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Override
    public List<Book> findByTitle(String title) {
        return catalogRepository.findAll()
                .stream()
                .filter(book -> book.getTitle().toLowerCase().startsWith(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findAll() {
        return catalogRepository.findAll();
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
    public void addBook(CreateBookCommand command) {
        catalogRepository.save(new Book(command.getTitle(), command.getAuthor(), command.getYear()));
    }

    @Override
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return catalogRepository.findById(command.getId())
                .map(bookToUpdate -> {
                    String title = command.getTitle();
                    if (!title.isBlank()) {
                        bookToUpdate.setTitle(title);
                    }
                    String author = command.getAuthor();
                    if (!author.isBlank()) {
                        bookToUpdate.setAuthor(author);
                    }
                    Integer year = command.getYear();
                    if (year > 0) {
                        bookToUpdate.setYear(year);
                    }
                    catalogRepository.save(bookToUpdate);
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

    }

}
