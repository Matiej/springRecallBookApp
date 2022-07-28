package com.testaarosa.spirngRecallBookApp.catalog.application;

import com.testaarosa.spirngRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.CreateBookCommand;
import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import com.testaarosa.spirngRecallBookApp.catalog.domain.CatalogRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
        return null;
    }

    @Override
    public Optional<Book> findOneByTitleAndAuthor(String title, String author) {
        return null;
    }

    @Override
    public void addBook(CreateBookCommand command) {
        catalogRepository.save(new Book(command.getTitle(), command.getAuthor(), command.getYear()));
    }

    @Override
    public void removeById(Long id) {

    }

    @Override
    public void updateBook() {

    }

}
