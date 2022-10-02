package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.catalog.CatalogTestBase;
import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CatalogController.class)
class CatalogControllerTest extends CatalogTestBase {
    @MockBean
    private CatalogUseCase catalogUseCase;
    @Autowired
    private CatalogController catalogController;

    @BeforeEach
    void setup(TestInfo testInfo) {
        log.info("Starting test: {}.", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Should getAll() give back books, no filer params, given limit 10.")
    public void shouldGetAllBooks() {
        //todo test this method with all options and params
        //given
        List<Book> givenBooks = prepareBooks();
        int limit = 10;
        when(catalogUseCase.findAllEager(Pageable.ofSize(limit)))
                .thenReturn(givenBooks);

        //when
        List<Book> resultBookList = catalogController.getAll(Optional.empty(),
                Optional.empty(), limit).getBody();

        //then
        assertNotNull(resultBookList);
        assertEquals(2, resultBookList.size());
        assertThat(resultBookList)
                .extracting("title", "year")
                .contains(tuple(givenBooks.get(0).getTitle(), givenBooks.get(0).getYear()),
                        tuple(givenBooks.get(1).getTitle(), givenBooks.get(1).getYear()));

    }

}
