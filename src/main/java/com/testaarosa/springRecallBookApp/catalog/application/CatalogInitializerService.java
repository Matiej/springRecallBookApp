package com.testaarosa.springRecallBookApp.catalog.application;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.testaarosa.springRecallBookApp.author.application.CreateAuthorCommand;
import com.testaarosa.springRecallBookApp.author.application.port.AuthorUseCase;
import com.testaarosa.springRecallBookApp.author.controller.AuthorQueryCommand;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogInitializer;
import com.testaarosa.springRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.springRecallBookApp.recipient.application.SaveRecipientCommand;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Slf4j
@Service
@AllArgsConstructor
class CatalogInitializerService implements CatalogInitializer {
    private final RecipientUseCase recipientUseCase;
    private final AuthorUseCase authorUseCase;
    private final CatalogUseCase catalogUseCase;

    @Override
//    @Transactional
    public void init() {
//        createRecipient();
        placeOrder();
        addBook();
    }

    private void addBook() {
        try (BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new ClassPathResource("books.csv").getInputStream()))) {
            CsvToBean<CsvBook> csvBooks = new CsvToBeanBuilder<CsvBook>(bufferReader)
                    .withType(CsvBook.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            csvBooks.forEach(this::createBookFromCVS);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read and parse CSV file.", e);
        }
    }

    private void createBookFromCVS(CsvBook csvbook) {
        Set<Author> authorFromCsv = createAuthorFromCsv(csvbook.getAuthors());
        Set<Long> authorsId = authorFromCsv.stream().map(Author::getId).collect(toSet());
        CreateBookCommand createBookCommand = new CreateBookCommand(
                csvbook.getTitle(),
                authorsId,
                Integer.valueOf(csvbook.getYear()),
                new BigDecimal(csvbook.getAmount()),
                10L);
        catalogUseCase.addBook(createBookCommand);
    }

    private Set<Author> createAuthorFromCsv(String nameAndLastName) {
        String[] splitAuthors = nameAndLastName.split(",");
        return Arrays.stream(splitAuthors)
                .filter(StringUtils::isNotBlank)
                .map(authors -> {
                    String[] authorNameAndLastName = authors.split(" ", 2);
                    log.info("Author from cvs file -> " + Arrays.toString(authorNameAndLastName));
                    CreateAuthorCommand.CreateAuthorCommandBuilder builder = CreateAuthorCommand.builder()
                            .name(authorNameAndLastName[0]);

                    AuthorQueryCommand.AuthorQueryCommandBuilder queryCommandBuilder = AuthorQueryCommand.builder()
                            .name(authorNameAndLastName[0])
                            .limit(1);

                    if (authorNameAndLastName.length > 1) {
                        builder.lastName(authorNameAndLastName[1]);
                        queryCommandBuilder.lastName(authorNameAndLastName[1]);
                    }
                    return authorUseCase.findAllByParams(queryCommandBuilder.build()).stream()
                            .findFirst().stream().peek(author -> log.info("Author found in data base with id: " + author.getId()
                                    + " name: " + author.getName() + ", lastName: " + author.getLastName() + "."))
                            .findFirst()
                            .orElseGet(() -> {
                                CreateAuthorCommand command = builder.build();
                                log.info("Saving new author from csv file: " + command.toString());
                                return authorUseCase.addAuthor(command);
                            });
                }).collect(toSet());
    }

    private void placeOrder() {

    }

    private void createRecipient() {
        SaveRecipientCommand recipient = SaveRecipientCommand.builder()
                .name("Ksawery Nowak")
                .phone("661555777")
                .street("Starej Drogi 11")
                .city("Warszawa")
                .zipCode("01-001")
                .email("ksawer@gmail.com")
                .build();
        recipientUseCase.addRecipient(recipient);

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CsvBook {
        @CsvBindByName
        private String title;
        @CsvBindByName
        private String authors;
        @CsvBindByName
        private String year;
        @CsvBindByName
        private String amount;
        @CsvBindByName
        private String thumbnail;

    }
}

