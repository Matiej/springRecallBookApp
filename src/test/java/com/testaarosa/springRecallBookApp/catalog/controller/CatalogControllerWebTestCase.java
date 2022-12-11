package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.catalog.CatalogTestBase;
import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@TestPropertySource("classpath:application-test.properties")
public class CatalogControllerWebTestCase extends CatalogTestBase {
    private final static String CATALOG_MAPPING = "/catalog";
    private MockMvc mockMvc;

    @Autowired
    private CatalogController catalogController;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private CatalogUseCase catalogUseCase;

    @BeforeEach
    void setup(TestInfo testInfo) {
        log.info("Starting test: {}.", testInfo.getDisplayName());
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @AfterEach
    void tierDown(TestInfo testInfo) {
        log.info("Finished test: {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Should getAll() perform GET method and gives back 200code response")
    void shouldGetAllBooks() throws Exception {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        int givenLimit = 3;
        List<RestBook> givenBooks = RestBook.toRestBooks(prepareBooks(), request);
        when(catalogUseCase.findAllEager(Pageable.ofSize(givenLimit))).thenReturn(prepareBooks());

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(CATALOG_MAPPING))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(header().string("Status", HttpStatus.OK.name()))
                .andExpect(header().string("Message", "Successful"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMethods(HttpMethod.GET)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(jsonInString(givenBooks)));

        //then
        verify(catalogUseCase, times(1)).findAllEager(Pageable.ofSize(givenLimit));
        verifyNoMoreInteractions(catalogUseCase);
    }

    @Test
    @DisplayName("Should getAll() perform GET method and gives back 200code, uses catalogUseCase.findByTitle for given title")
    void shouldGetAllBooksForGivetTitle() throws Exception {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        int givenLimit = 3;
        List<RestBook> givenBooks = RestBook.toRestBooks(prepareBooks(), request);
        String some_title = "Effective Java";

        //when
        when(catalogUseCase.findByTitle(some_title, Pageable.ofSize(givenLimit))).thenReturn(prepareBooks());

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(CATALOG_MAPPING)
                        .param("title", some_title))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(header().string("Status", HttpStatus.OK.name()))
                .andExpect(header().string("Message", "Successful"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMethods(HttpMethod.GET)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(jsonInString(givenBooks)))
                .andReturn();

        //then
        verify(catalogUseCase, times(1)).findByTitle(some_title, Pageable.ofSize(givenLimit));
        verifyNoMoreInteractions(catalogUseCase);
    }

    @Test
    @DisplayName("Should getAll() perform GET method and gives back 200code, uses catalogUseCase.findByAuthor for given author")
    void shouldGetAllBooksForGivenAuthor() throws Exception {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        int givenLimit = 3;
        List<RestBook> givenBooks = RestBook.toRestBooks(prepareBooks(), request);
        String author = "Jon Smith";

        //when
        when(catalogUseCase.findByAuthor(author, Pageable.ofSize(givenLimit))).thenReturn(prepareBooks());

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(CATALOG_MAPPING)
                        .param("author", author))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(header().string("Status", HttpStatus.OK.name()))
                .andExpect(header().string("Message", "Successful"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMethods(HttpMethod.GET)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(jsonInString(givenBooks)))
                .andReturn();

        //then
        verify(catalogUseCase, times(1)).findByAuthor(author, Pageable.ofSize(givenLimit));
        verifyNoMoreInteractions(catalogUseCase);
    }

    @Test
    @DisplayName("Should getAll() perform GET method and gives back 200code, uses catalogUseCase.findByTitleAndAuthor for given title & author")
    void shouldGetAllBooksForGivenAuthorAndTitle() throws Exception {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        int givenLimit = 3;
        List<RestBook> givenBooks = RestBook.toRestBooks(prepareBooks(), request);
        String some_title = "Effective Java";
        String author = "Jon Smith";

        //when
        when(catalogUseCase.findByTitleAndAuthor(some_title, author, Pageable.ofSize(givenLimit))).thenReturn(prepareBooks());

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(CATALOG_MAPPING)
                        .param("author", author)
                        .param("title", some_title))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(header().string("Status", HttpStatus.OK.name()))
                .andExpect(header().string("Message", "Successful"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMethods(HttpMethod.GET)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(jsonInString(givenBooks)))
                .andReturn();

        //then
        verify(catalogUseCase, times(1)).findByTitleAndAuthor(some_title, author, Pageable.ofSize(givenLimit));
        verifyNoMoreInteractions(catalogUseCase);
    }

    @Test
    @DisplayName("Should getAll() NOT perform GET method and gives back 404code response")
    void shouldGetAllBooksWrongUri() throws Exception {
        //given

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(CATALOG_MAPPING + "badURI"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @DisplayName("Should getAll() NOT perform GET method and gives back 400code, wrong limit param")
    void shouldGetAllBooksForGivenAuthorAndTitleWrongLimitParam() throws Exception {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String givenLimit = "0";
        List<RestBook> givenBooks = RestBook.toRestBooks(prepareBooks(), request);
        String some_title = "Effective Java";
        String author = "Jon Smith";

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(CATALOG_MAPPING)
                        .param("author", author)
                        .param("title", some_title)
                        .param("limit", givenLimit))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).contains("Page size cannot be less than one"))
                .andExpect(header().string("Message", "getAll.limit: Page size cannot be less than one"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @DisplayName("Should getBookById() perform GET method and gives back 200code response")
    void shouldGetBookById() throws Exception {
        //given
        Long bookId = 3L;
        Book book = prepareBooks().get(0);
        when(catalogUseCase.findById(bookId)).thenReturn(Optional.of(book));

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(CATALOG_MAPPING + "/{id}", bookId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Status", HttpStatus.OK.name()))
                .andExpect(header().string("Message", "Successful"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMethods(HttpMethod.GET)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(jsonInString(book)));

        //then
        verify(catalogUseCase, times(1)).findById(bookId);
        verifyNoMoreInteractions(catalogUseCase);
    }

    @Test
    @DisplayName("Should getByBookId() perform GET method and gives back 404code, empty Optional return")
    void shouldGetBookByIdNotFound() throws Exception {
        //given
        Long bookId = 11111L;
        String headerErrorMessage = "Book with ID: " + bookId + " not found!";
        when(catalogUseCase.findById(bookId)).thenReturn(Optional.empty());

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(CATALOG_MAPPING + "/{id}", bookId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string("Status", HttpStatus.NOT_FOUND.name()))
                .andExpect(result -> assertThat(result.getResponse().getHeader("Message")).contains(headerErrorMessage))
                .andReturn();

        //then
        verify(catalogUseCase, times(1)).findById(bookId);
        verifyNoMoreInteractions(catalogUseCase);
    }

    @Test
    @DisplayName("Should getBookById() NOT perform GET method and gives back 404code response")
    void shouldGetBookByIdWrongUri() throws Exception {
        //given
        Long bookId = 1L;
        Book book = prepareBooks().get(0);
        when(catalogUseCase.findById(bookId)).thenReturn(Optional.of(book));

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(CATALOG_MAPPING + "/{id}/ranomuri", bookId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @DisplayName("Should getBookById() NOT perform GET method and gives back 400code, id = 0")
    void shouldGetBookByIdForWithWrongIdParam() throws Exception {
        //given
        Long bookId = 0L;

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(CATALOG_MAPPING + "/{id}", bookId))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).contains("BookId field value must be greater than 0"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 201code response")
    void shouldAddNewBook() throws Exception {
        //given
        Book book = prepareBooks().get(0);
        RestBookCommand command = prepareRestBookCommand();
        when(catalogUseCase.addBook(command.toCreateBookCommand())).thenReturn(book);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(header().string("Status", HttpStatus.CREATED.name()))
                .andExpect(header().string("Message", "Successful"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowedMethods(HttpMethod.POST)))
                .andReturn();

        //then
        verify(catalogUseCase, times(1)).addBook(command.toCreateBookCommand());
        verifyNoMoreInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 400code response, Empty title")
    void shouldAddNewBookValidationTitle() throws Exception {
        //given
        Book book = prepareBooks().get(0);
        RestBookCommand command = prepareRestBookCommand();
        command.setTitle(StringUtils.EMPTY);
        when(catalogUseCase.addBook(command.toCreateBookCommand())).thenReturn(book);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).contains("Title field can't be blank, empty or null"))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 400code response, Null title")
    void shouldAddNewBookValidationTitleNull() throws Exception {
        //given
        Book book = prepareBooks().get(0);
        RestBookCommand command = prepareRestBookCommand();
        command.setTitle(null);
        when(catalogUseCase.addBook(command.toCreateBookCommand())).thenReturn(book);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).contains("Title field can't be blank, empty or null"))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 400code response, year = null")
    void shouldAddNewBookValidationYearNull() throws Exception {
        //given
        Book book = prepareBooks().get(0);
        RestBookCommand command = prepareRestBookCommand();
        command.setYear(null);
        when(catalogUseCase.addBook(command.toCreateBookCommand())).thenReturn(book);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).contains("Year filed can't be null"))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 400code response, year more than 4 digits")
    void shouldAddNewBookValidationYearToMuchDigits() throws Exception {
        //given
        Book book = prepareBooks().get(0);
        RestBookCommand command = prepareRestBookCommand();
        command.setYear(19999);
        when(catalogUseCase.addBook(command.toCreateBookCommand())).thenReturn(book);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).contains("yearOfBirth filed expects 4 digit value"))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 400code response, price = null")
    void shouldAddNewBookValidationPriceNull() throws Exception {
        //given
        Book book = prepareBooks().get(0);
        RestBookCommand command = prepareRestBookCommand();
        command.setPrice(null);
        when(catalogUseCase.addBook(command.toCreateBookCommand())).thenReturn(book);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).contains("price filed can't be null"))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 400code response, price is negative")
    void shouldAddNewBookValidationPriceNegative() throws Exception {
        //given
        Book book = prepareBooks().get(0);
        RestBookCommand command = prepareRestBookCommand();
        command.setPrice(new BigDecimal("-101.01"));
        when(catalogUseCase.addBook(command.toCreateBookCommand())).thenReturn(book);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .contains("Price value can't be negative, min price value is 0.00"))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 400code response, no authors!")
    void shouldAddNewBookValidationNoAuthors() throws Exception {
        //given
        RestBookCommand command = prepareRestBookCommand();
        Set<Long> authors = new HashSet<>();
        command.setAuthors(authors);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .contains("Book needs any author!"))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 400code response, authors = null")
    void shouldAddNewBookValidationAuthorsNUll() throws Exception {
        //given
        RestBookCommand command = prepareRestBookCommand();
        command.setAuthors(null);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .contains("Book needs any author!"))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 400code response, available value is negative")
    void shouldAddNewBookValidationAvailableNegative() throws Exception {
        //given
        Book book = prepareBooks().get(0);
        RestBookCommand command = prepareRestBookCommand();
        command.setAvailable(-1L);
        when(catalogUseCase.addBook(command.toCreateBookCommand())).thenReturn(book);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .contains("available filed must be positive or zero"))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should addBook() perform POST method and gives back 400code response, available value is null")
    void shouldAddNewBookValidationAvailableNull() throws Exception {
        //given
        Book book = prepareBooks().get(0);
        RestBookCommand command = prepareRestBookCommand();
        command.setAvailable(null);
        when(catalogUseCase.addBook(command.toCreateBookCommand())).thenReturn(book);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.post(CATALOG_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .contains("available filed can't be null"))
                .andReturn();

        //then
        verifyNoInteractions(catalogUseCase);
    }

    private RestBookCommand prepareRestBookCommand() {
        RestBookCommand command = new RestBookCommand();
        command.setTitle("Effective Java");
        command.setAvailable(12L);
        command.setYear(2005);
        command.setPrice(new BigDecimal("101.82"));
        command.setAuthors(Set.of(1L));

        return command;
    }


}
