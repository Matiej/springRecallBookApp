package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.author.dataBase.AuthorJpaRepository;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.catalog.CatalogTestBase;
import com.testaarosa.springRecallBookApp.catalog.application.CreateBookCommand;
import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class CatalogControllerTestIT extends CatalogTestBase {

    @Autowired
    private CatalogController catalogController;
    @Autowired
    private AuthorJpaRepository authorJpaRepository;
    @Autowired
    private CatalogUseCase catalogUseCase;

    @BeforeEach
    void setup(TestInfo testInfo) {
        log.info("Starting test: {}.", testInfo.getDisplayName());
        prepareAndAddBooks();
    }

    @Test
    @DisplayName("Should getAll() will find 2 books saved in H2 database. No params given to the method.")
    void getAllBooksTest() {
        //given

        //when
        ResponseEntity<List<Book>> catalogControllerAll = catalogController.getAll(Optional.empty(), Optional.empty(), 10);

        //then
        assertEquals(2, catalogControllerAll.getBody().size());

    }

    @Test
    @DisplayName("Should placeOrder() will find 1 book saved in H2 database. Author name give.")
    void getAllBooksForGivenAuthorTest() {
        //given
        String givenAuthorName = "Brian";
        String expectedBookTitle = "Mama mia";

        //when
        ResponseEntity<List<Book>> catalogControllerAll = catalogController.getAll(Optional.empty(),
                Optional.of(givenAuthorName), 10);
        List<Book> books = catalogControllerAll.getBody();

        //then
        assertEquals(1, books.size());
        assertEquals(expectedBookTitle, books.get(0).getTitle());
    }

    @Test
    @Transactional
    @DisplayName("Should getAll() will find 2 authors for given book title.")
    void getAllBooksForGivenTitleTest() {
        //given
        String givenBookTitle = "Mama mia";
        List<Author> givenAuthors = prepareAuthors().stream().sorted(Comparator.comparing(Author::getLastName)).toList();

        //when
        ResponseEntity<List<Book>> catalogControllerAll = catalogController.getAll(
                Optional.of(givenBookTitle),
                Optional.empty(),
                10);

        List<Book> books = catalogControllerAll.getBody();
        List<Author> linkedAuthors = books.get(0).getLinkedAuthors().stream().sorted(Comparator.comparing(Author::getLastName)).toList();
        //then
        assertEquals(1, books.size());
        assertEquals(givenBookTitle, books.get(0).getTitle());
        assertAll("Authors properties test",
                () -> assertAll("Check authors list",
                        () -> assertNotNull(linkedAuthors),
                        () -> assertEquals(2, linkedAuthors.size())),
                () -> assertAll("First author test",
                        () -> assertEquals(givenAuthors.get(0).getName(), linkedAuthors.get(0).getName()),
                        () -> assertEquals(givenAuthors.get(0).getLastName(), linkedAuthors.get(0).getLastName())),
                () -> assertAll("Second author test",
                        () -> assertEquals(givenAuthors.get(1).getName(), linkedAuthors.get(1).getName()),
                        () -> assertEquals(givenAuthors.get(1).getLastName(), linkedAuthors.get(1).getLastName()))
        );
    }

    @Test
    @DisplayName("Should getAll() throws IllegalArgumentException for given title and LIMIT zero.")
    void getAllBooksForGivenTitleWithLimitZeroTest() {
        //given
        String givenBookTitle = "Mama mia";
        int givenLimit = 0;
        String expectedErrorMessage = "Page size cannot be less than one";

        //when
        Throwable throwable = catchThrowable(() -> catalogController.getAll(
                Optional.of(givenBookTitle),
                Optional.empty(),
                givenLimit));

        //then
        then(throwable).as("An ConstraintViolationException should be thrown if limit is less then 1")
                .isInstanceOf(ConstraintViolationException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }


    private List<Book> prepareAndAddBooks() {
        List<Author> authors = authorJpaRepository.saveAll(prepareAuthors());
        CreateBookCommand effective_java = CreateBookCommand.builder()
                .authors(Set.of(authors.get(0).getId()))
                .title("Effective Java")
                .year(2005)
                .price(new BigDecimal(50))
                .availAble(50L).build();
        Book javaBook = catalogUseCase.addBook(effective_java);

        CreateBookCommand mama_mia = CreateBookCommand.builder()
                .authors(Set.of(authors.get(0).getId(), authors.get(1).getId()))
                .title("Mama mia")
                .year(2015)
                .price(new BigDecimal(10))
                .availAble(12L).build();
        Book mamaMinaBook = catalogUseCase.addBook(mama_mia);

        return Arrays.asList(javaBook, mamaMinaBook);
    }

    private List<Author> prepareAuthors() {
        Author stefan = new Author("Stefan", "Konieczny", 1850);
        Author brian = new Author("Brian", "Goetz", 1990);
        return List.of(stefan, brian);
    }

}