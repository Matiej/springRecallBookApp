package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.catalog.CatalogTestBase;
import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CatalogControllerApiTest extends CatalogTestBase {
    @MockBean
    private CatalogUseCase catalogUseCase;
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup(TestInfo testInfo) {
        log.info("Starting test: {}.", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Should getAll() method API .......") //todo nice name
    public void getAllBooks() {
        //given
        List<Book> givenBooks = prepareBooks();
        int givenLimit = 3;
        when(catalogUseCase.findAllEager(Pageable.ofSize(givenLimit))).thenReturn(givenBooks);

        ParameterizedTypeReference<List<Book>> type = new ParameterizedTypeReference<List<Book>>() {
        };

        //when
        String url = "http://localhost:" + port + "/catalog";
        RequestEntity<Void> request  = RequestEntity.get(URI.create(url)).build();
        ResponseEntity<List<Book>> response = restTemplate.exchange(request, type);
        List<Book> responseBody = response.getBody();

        //then
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.size()).isEqualTo(2);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody)
                .extracting("title", "year")
                .contains(tuple(givenBooks.get(0).getTitle(), givenBooks.get(0).getYear()),
                        tuple(givenBooks.get(1).getTitle(), givenBooks.get(1).getYear()));
    }
}
