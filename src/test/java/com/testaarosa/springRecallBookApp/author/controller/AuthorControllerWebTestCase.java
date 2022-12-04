package com.testaarosa.springRecallBookApp.author.controller;

import com.testaarosa.springRecallBookApp.author.AuthorTestBase;
import com.testaarosa.springRecallBookApp.author.application.UpdateAuthorCommand;
import com.testaarosa.springRecallBookApp.author.application.UpdatedAuthorResponse;
import com.testaarosa.springRecallBookApp.author.application.port.AuthorUseCase;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
class AuthorControllerWebTestCase extends AuthorTestBase {
    private final static String AUTHORS_MAPPING = "/authors";
    private MockMvc mockMvc;
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @Autowired
    private AuthorController authorController;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private AuthorUseCase authorUseCase;


    @BeforeEach
    @DisplayName("Should getAll() perform GET method and gives back 200code response")
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
    @WithMockUser(username = "user", password = "pass", roles = {"USER"})
    @DisplayName("Should getAll() perform GET method and gives back 200code response")
    void shouldGetAllAuthors() throws Exception {
        //given
        int limit = 3;
        List<Author> givenAuthors = prepareAuthors();
        AuthorQueryCommand authorQueryCommand = AuthorQueryCommand
                .builder()
                .name(null)
                .lastName(null)
                .yearOfBirth(null)
                .limit(limit).build();

        //when
        when(authorUseCase.findAllByParams(authorQueryCommand)).thenReturn(givenAuthors);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(AUTHORS_MAPPING))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(jsonInString(givenAuthors)));

        //then
        verify(authorUseCase, times(1)).findAllByParams(authorQueryCommand);
        verifyNoMoreInteractions(authorUseCase);

    }

    @Test
    @DisplayName("Should getAll() NOT perform GET method, wrong uri and gives back 404code response")
    void shouldGetAllAuthorsWrongUri() throws Exception {
        //given
        int limit = 3;
        List<Author> givenAuthors = prepareAuthors();
        AuthorQueryCommand authorQueryCommand = AuthorQueryCommand
                .builder()
                .name(null)
                .lastName(null)
                .yearOfBirth(null)
                .limit(limit).build();

        //when
        when(authorUseCase.findAllByParams(authorQueryCommand)).thenReturn(givenAuthors);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(AUTHORS_MAPPING + "bad"))
                .andDo(print())
                .andExpect(status().is(404))
                .andReturn();

        //then
        verify(authorUseCase, times(0)).findAllByParams(authorQueryCommand);
        verifyNoMoreInteractions(authorUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"USER"})
    @DisplayName("Should findOneById() perform GET method, and gives back 200code response")
    void shouldFindOneAuthorById() throws Exception {
        //given
        Long id = 1L;
        Author author = prepareAuthors().get(0);

        //when
        when(authorUseCase.findById(id)).thenReturn(Optional.of(author));

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(AUTHORS_MAPPING + "/{id}", id))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(jsonInString(author)));

        //then
        verify(authorUseCase, times(1)).findById(id);
        verifyNoMoreInteractions(authorUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"USER"})
    @DisplayName("Should findOneById() perform GET method, and doesn't give back object 404code response. Optional.empty() Result")
    void shouldFindOneAuthorByIdWrongId() throws Exception {
        //given
        Long id = 232L;
        Author author = prepareAuthors().get(0);

        //when
        when(authorUseCase.findById(id)).thenReturn(Optional.empty());

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(AUTHORS_MAPPING + "/{id}", id))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(header().string("Status", HttpStatus.NOT_FOUND.name()))
                .andExpect(header().string("Message", "Author with ID: " + id + " not found!"))
                .andReturn();

        //then
        verify(authorUseCase, times(1)).findById(id);
        verifyNoMoreInteractions(authorUseCase);
    }

    @Test
    @DisplayName("Should findOneById() perform GET method, and doesn't give back object 404code response. Optional.empty() Result")
    void shouldFindOneAuthorByIdWrongUri() throws Exception {
        //given
        Long id = 1L;
        Author author = prepareAuthors().get(0);

        //when
        when(authorUseCase.findById(id)).thenReturn(Optional.of(author));

        //expect
        mockMvc.perform(MockMvcRequestBuilders.get(AUTHORS_MAPPING + "bad" + "/{id}", id))
                .andDo(print())
                .andExpect(status().is(404))
                .andReturn();

        //then
        verifyNoInteractions(authorUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should updateAuthor() perform Patch method, and gives back 202code response")
    void shouldUpdateAuthor() throws Exception {
        //given
        Long id = 1L;
        RestAuthorCommand command = new RestAuthorCommand("Jaro", "Skorwon", 1979);
        UpdateAuthorCommand updateAuthorCommand = command.toUpdateAuthorCommand(id);
        HttpMethod[] allowedMethods = {HttpMethod.PATCH};

        URI expectedLocation = ServletUriComponentsBuilder
                .fromContextPath(request)
                .path("/catalog")
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        //when
        when(authorUseCase.updateAuthor(updateAuthorCommand)).thenReturn(UpdatedAuthorResponse.SUCCESS);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.patch(AUTHORS_MAPPING + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(updateAuthorCommand)))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(header().string(HttpHeaders.LOCATION, expectedLocation.toString()))
                .andExpect(header().string("Status", HttpStatus.ACCEPTED.name()))
                .andExpect(header().string("Message", "Successful"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, Arrays.toString(allowedMethods)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //then
        verify(authorUseCase, times(1)).updateAuthor(updateAuthorCommand);
        verifyNoMoreInteractions(authorUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should updateAuthor() perform Post method, and gives back 204code response")
    void shouldNotUpdateAuthor() throws Exception {
        //given
        Long id = 1L;
        RestAuthorCommand command = new RestAuthorCommand("Jaro", "Skorwon", 1979);
        UpdateAuthorCommand updateAuthorCommand = command.toUpdateAuthorCommand(id);
        UpdatedAuthorResponse updatedAuthorResponse = UpdatedAuthorResponse.FAILURE(List.of("Author not found for update with ID: " + id));

        //when
        when(authorUseCase.updateAuthor(updateAuthorCommand)).thenReturn(updatedAuthorResponse);

        //expect
        mockMvc.perform(MockMvcRequestBuilders.patch(AUTHORS_MAPPING + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(updateAuthorCommand)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(header().string("Status", HttpStatus.NO_CONTENT.name()))
                .andExpect(header().string("Message", updatedAuthorResponse.getErrorList().toString()))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.PATCH.name()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //then
        verify(authorUseCase, times(1)).updateAuthor(updateAuthorCommand);
        verifyNoMoreInteractions(authorUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should updateAuthor() perform Patch method, and gives 400code, validation ID error. ")
    void shouldNotUpdateAuthorValidationID() throws Exception {
        //given
        Long id = 0L;
        RestAuthorCommand command = new RestAuthorCommand("Jaro", "Skorwon", 1979);
        UpdateAuthorCommand updateAuthorCommand = command.toUpdateAuthorCommand(id);

        //when
        //expect
        mockMvc.perform(MockMvcRequestBuilders.patch(AUTHORS_MAPPING + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonInString(updateAuthorCommand)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(header().string("Message", "updateAuthor.id: AuthorId field value must be greater than 0"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //then
        verifyNoInteractions(authorUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should updateAuthor() perform Patch method, and gives 400code, validation year error. ")
    void shouldNotUpdateAuthorValidationYear() throws Exception {
        //given
        Long id = 1L;
        RestAuthorCommand command = new RestAuthorCommand("Jaro", "Skorwon", 197911);
        UpdateAuthorCommand updateAuthorCommand = command.toUpdateAuthorCommand(id);

        //when
        when(authorUseCase.updateAuthor(updateAuthorCommand)).thenReturn(UpdatedAuthorResponse.SUCCESS);

        //expect
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.patch(AUTHORS_MAPPING + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON).content(jsonInString(updateAuthorCommand)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Status", HttpStatus.BAD_REQUEST.name()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //then
        String message = mvcResult.getResponse().getHeader("Message");
        assertThat(message).contains("yearOfBirth filed expects 4 digit value");
        assertThat(message).contains("rejected value [197911]");
        verifyNoInteractions(authorUseCase);
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = {"ADMIN"})
    @DisplayName("Should removeById() perform Delete method, and gives back 204code response. ForceDelete is false")
    void shouldDeleteAuthorByIdForceFalse() throws Exception {
        //given
        Long id = 1L;
        boolean isForceDelete = false;
        //when
        //expect
        mockMvc.perform(MockMvcRequestBuilders.delete(AUTHORS_MAPPING + "/{id}", id)
                        .param("isForceDelete", Boolean.toString(isForceDelete)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        verify(authorUseCase, times(1)).removeById(id, isForceDelete);
        verifyNoMoreInteractions(authorUseCase);
    }
}
