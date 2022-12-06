package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.catalog.CatalogTestBase;
import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.ConstraintViolationException;
import java.util.List;

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
        MockHttpServletRequest request = new MockHttpServletRequest();
        int givenLimit = 3;
        List<RestBook> givenBooks = RestBook.toRestBooks(prepareBooks(), request);

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


}
