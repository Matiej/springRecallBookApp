package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.catalog.CatalogTestBase;
import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.globalExceptionHandler.RestControllerExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(SpringExtension.class)
public class CatalogControllerWebTest extends CatalogTestBase {
    @Mock
    private CatalogUseCase catalogUseCase;
    @InjectMocks
    private CatalogController catalogController;
    private MockMvc mockMvc;

    @BeforeEach
    void setup(TestInfo testInfo) {
        log.info("Starting test: {}.", testInfo.getDisplayName());
        mockMvc = MockMvcBuilders.standaloneSetup(catalogController)
                .setControllerAdvice(new RestControllerExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should getAll() perform GET method and gives back 200code response")
    public void shouldGetAllBooks() throws Exception {
        //given
        int givenLimit = 3;
        List<Book> givenBooks = prepareBooks();
        when(catalogUseCase.findAllEager(Pageable.ofSize(givenLimit))).thenReturn(givenBooks);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get("/catalog"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(jsonInString(givenBooks)));
    }
}
