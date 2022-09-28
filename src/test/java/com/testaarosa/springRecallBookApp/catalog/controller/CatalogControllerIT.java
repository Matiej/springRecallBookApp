package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.author.dataBase.AuthorJpaRepository;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.catalog.application.CreateBookCommand;
import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class CatalogControllerIT {

    @Autowired
    CatalogController catalogController;
    @Autowired
    AuthorJpaRepository authorJpaRepository;
    @Autowired
    CatalogUseCase catalogUseCase;

    @Test
    public void getAllBooksTest() {
        //given
        List<Book> books = prepareAdnAddBooks();

        //when
        ResponseEntity<List<Book>> catalogControllerAll = catalogController.getAll(Optional.empty(), Optional.empty(), 10);

        //then
        assertEquals(2, catalogControllerAll.getBody().size());

    }

    private List<Book> prepareAdnAddBooks() {
        List<Author> authors = prepareAndSaveAuthors();
        CreateBookCommand effective_java = CreateBookCommand.builder()
                .authors(Set.of(authors.get(0).getId()))
                .title("Effective Java")
                .year(2005)
                .price(new BigDecimal(50))
                .availAble(50L).build();
        Book javaBook = catalogUseCase.addBook(effective_java);

        CreateBookCommand mama_mia = CreateBookCommand.builder()
                .authors(Set.of(authors.get(1).getId(), authors.get(0).getId()))
                .title("Mama mia")
                .year(2015)
                .price(new BigDecimal(10))
                .availAble(12L).build();
        Book mamaMinaBook = catalogUseCase.addBook(mama_mia);

        return Arrays.asList(javaBook,mamaMinaBook);
    }

    private List<Author> prepareAndSaveAuthors() {
        Author stefan = authorJpaRepository.save(new Author("Stefan", "Konieczny", 1850));
        Author brian = authorJpaRepository.save(new Author("Brian", "Goetz", 1990));
        return List.of(stefan, brian);
    }

}