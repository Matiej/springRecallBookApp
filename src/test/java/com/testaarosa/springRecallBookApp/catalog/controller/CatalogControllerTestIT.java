package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.author.dataBase.AuthorJpaRepository;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.catalog.CatalogTestBase;
import com.testaarosa.springRecallBookApp.catalog.application.CreateBookCommand;
import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.globalHeaderFactory.HeaderKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    }

    @Test
    @DisplayName("Should getAll() will find 2 books saved in H2 database. No params given to the method.")
    void getAllBooksTest() {
        //given
        prepareAndAddBooks();
        //when
        ResponseEntity<List<Book>> catalogControllerAll = catalogController.getAll(Optional.empty(), Optional.empty(), 10);

        //then
        List<Book> books = catalogControllerAll.getBody();
        assertNotNull(books);
        assertEquals(2, books.size());

    }

    @Test
    @DisplayName("Should placeOrder() will find 1 book saved in H2 database. Author name give.")
    void getAllBooksForGivenAuthorTest() {
        //given
        prepareAndAddBooks();
        String givenAuthorName = "Brian";
        String expectedBookTitle = "Mama mia";

        //when
        ResponseEntity<List<Book>> catalogControllerAll = catalogController.getAll(Optional.empty(),
                Optional.of(givenAuthorName), 10);
        List<Book> books = catalogControllerAll.getBody();

        //then
        assertNotNull(books);
        assertEquals(1, books.size());
        assertEquals(expectedBookTitle, books.get(0).getTitle());
    }

    @Test
    @Transactional
    @DisplayName("Should getAll() will find 2 authors for given book title.")
    void getAllBooksForGivenTitleTest() {
        //given
        prepareAndAddBooks();
        String givenBookTitle = "Mama mia";
        List<Author> givenAuthors = prepareAuthors().stream().sorted(Comparator.comparing(Author::getLastName)).toList();

        //when
        ResponseEntity<List<Book>> catalogControllerAll = catalogController.getAll(
                Optional.of(givenBookTitle),
                Optional.empty(),
                10);

        List<Book> books = catalogControllerAll.getBody();
        //then
        assertNotNull(books);
        List<Author> linkedAuthors = books.get(0).getLinkedAuthors().stream().sorted(Comparator.comparing(Author::getLastName)).toList();
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
        prepareAndAddBooks();
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

    @Test
    @DisplayName("Should getAll() find 1of2 books with LIMIT 1.")
    void getAllBooksForGivenLimitTest() {
        //given
        prepareAndAddBooks();
        int givenLimit = 1;

        //when
        ResponseEntity<List<Book>> response = catalogController.getAll(
                Optional.empty(),
                Optional.empty(),
                givenLimit);

        //then
        HttpStatus statusCode = response.getStatusCode();
        List<Book> books = response.getBody();
        assertAll("Check if all results are not null",
                () -> assertNotNull(response),
                () -> assertNotNull(statusCode),
                () -> assertNotNull(books));
        assertEquals(givenLimit, books.size());
        assertEquals(HttpStatus.OK, statusCode);
    }

    @Test
    @DisplayName("Should getBookById() find book in data data base. Given correct id")
    public void shouldGetBookById() {
        //given
        prepareAndAddBooks();
        Long givenBookId = 1L;
        String bookTitle = "Effective Java";
        Integer year = 2005;
        Long available = 50L;
        BigDecimal price = new BigDecimal(50.00);

        //when
        ResponseEntity<Book> response = catalogController.getBookById(givenBookId);

        //then
        Book book = response.getBody();
        HttpStatus statusCode = response.getStatusCode();
        assertAll("Check if results are not null",
                () -> assertNotNull(response),
                () -> assertNotNull(book),
                () -> assertNotNull(statusCode));
        assertEquals(HttpStatus.OK, statusCode);
        assertAll("Check book fields if it is correct one",
                () -> assertEquals(bookTitle, book.getTitle()),
                () -> assertEquals(year, book.getYear()),
                () -> assertEquals(available, book.getAvailable()),
                () -> assertTrue(price.compareTo(book.getPrice()) > -1));
    }

    @Test
    @DisplayName("Should getBookById() not find book. Given not exist ID")
    public void shouldNotGetBookById() {
        //given
        Long givenBookId = 1111L;
        String expectedMessage = "Book with ID: " + givenBookId + " not found!";

        //when
        ResponseEntity<Book> response = catalogController.getBookById(givenBookId);

        //then
        Book book = response.getBody();
        HttpStatus statusCode = response.getStatusCode();
        HttpHeaders headers = response.getHeaders();
        List<String> message = headers.get(HeaderKey.MESSAGE.getHeaderKeyLabel());
        assertAll("Check results",
                () -> assertNotNull(response),
                () -> assertNull(book),
                () -> assertNotNull(statusCode),
                () -> assertNotNull(headers),
                () -> assertNotNull(message),
                () -> assertTrue(message.size() > 0));
        assertAll("Assert response headers and status",
                () -> assertEquals(expectedMessage, message.get(0)),
                () -> assertEquals(HttpStatus.NOT_FOUND, statusCode));
    }

    @Test
    @DisplayName("Should getBookById() throws exception. Given negative ID")
    public void shouldThrowExceptionGetBookByNegativeId() {
        //given
        Long givenBookId = -1111L;
        String expectedErrorMessage = "BookId field value must be greater than 0";

        //when
        Throwable throwable = catchThrowable(() -> catalogController.getBookById(givenBookId));

        //then
        then(throwable).as("An ConstraintViolationException should be thrown if ID is less then 1")
                .isInstanceOf(ConstraintViolationException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @DisplayName("Should addBook() create and add book into DB")
    public void shouldAddBook() {
        //given
        RestBookCommand restBookCommand = prepareRestBookCommand();
        List<Author> authors = prepareAuthors();
        authorJpaRepository.saveAll(authors);

        //when
        ResponseEntity<Void> response = catalogController.addBook(restBookCommand);

        //then
        assertNotNull(response);
        List<String> location = response.getHeaders().get("Location");
        assertAll("Check response headers, status code and saved URI ",
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertNotNull(location),
                () -> assertTrue(location.size() > 0),
                () -> assertEquals("http://localhost/catalog/1", location.get(0)));
    }

    @Test
    @DisplayName("Should addBook() throws exception because no author in DB")
    public void shouldNotAddBookAndThrowsIllegalArgumentException() {
        //given
        RestBookCommand restBookCommand = prepareRestBookCommand();
        List<Author> authors = prepareAuthors();
        String expectedErrorMessage = "No author found with ID: 1";

        //when
        Throwable throwable = catchThrowable(() -> catalogController.addBook(restBookCommand));

        //then
        then(throwable).as("An IllegalArgumentException should be thrown if no given author in data base")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @DisplayName("Should updateBook() create and add book into DB")
    @Transactional
    public void shouldUpdateBook() {
        //given
        List<Author> authors = prepareAuthors();
        authorJpaRepository.saveAll(authors);
        RestBookCommand restBookCommand = prepareRestBookCommand();
        catalogUseCase.addBook(restBookCommand.toCreateBookCommand());

        //when
        String titleToUpdate = "Updated Title 2";
        restBookCommand.setTitle(titleToUpdate);
        restBookCommand.setAvailable(100L);
        ResponseEntity<Object> response = catalogController.updateBook(1L, restBookCommand);
        Book updatedBook = catalogUseCase.findOne(1L);

        //then
        assertNotNull(response);
        List<String> location = response.getHeaders().get("Location");
        assertAll("Check response headers, status code and saved URI ",
                () -> assertEquals(HttpStatus.ACCEPTED, response.getStatusCode()),
                () -> assertNotNull(location),
                () -> assertTrue(location.size() > 0),
                () -> assertEquals("http://localhost/catalog/1", location.get(0)));
        assertAll("Check updated book fields",
                () -> assertEquals(titleToUpdate, updatedBook.getTitle()),
                () -> assertEquals(100L, updatedBook.getAvailable()));
    }


    private RestBookCommand prepareRestBookCommand() {
        RestBookCommand effectiveJava2RestCommand = new RestBookCommand(
                "Effective Java 2",
                2006,
                new BigDecimal(10),
                Set.of(1L),
                10L);
        return effectiveJava2RestCommand;
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