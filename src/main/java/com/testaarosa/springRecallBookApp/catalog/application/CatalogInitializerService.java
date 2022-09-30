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
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderItem;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderRecipient;
import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import com.testaarosa.springRecallBookApp.recipient.application.SaveRecipientCommand;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@AllArgsConstructor
class CatalogInitializerService implements CatalogInitializer {
    private final RecipientUseCase recipientUseCase;
    private final AuthorUseCase authorUseCase;
    private final CatalogUseCase catalogUseCase;
    private final OrderUseCase orderUseCase;

    @Override
    @Transactional
    public void init() {
        addBook();
        createRecipient();
        placeOrder();
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
        Book book = catalogUseCase.addBook(createBookCommand);
        catalogUseCase.updateBookCover(updateBookCoverFromCsv(book, csvbook.getThumbnail()));

    }

    private UpdateBookCoverCommand updateBookCoverFromCsv(Book book, String bookThumbnail) {
        UpdateBookCoverCommand.UpdateBookCoverCommandBuilder updateBookCoverCommandBuilder = UpdateBookCoverCommand.builder();
        try {
            URL thumbnailURL = new URL(bookThumbnail);
            URLConnection urlConnection = thumbnailURL.openConnection();
            String contentType = urlConnection.getContentType();
            byte[] bookCover = IOUtils.toByteArray(thumbnailURL);
            updateBookCoverCommandBuilder.id(book.getId())
                    .file(bookCover)
                    .fileName(prepareFileName(book.getTitle(), contentType))
                    .fileContentType(contentType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return updateBookCoverCommandBuilder.build();
    }

    private String prepareFileName(String bookTitle, String contentType) {
        return new StringJoiner(".")
                .add(StringUtils.deleteWhitespace(bookTitle).toLowerCase())
                .add(MimeType.getExtensionByContentType(contentType))
                .toString();
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

    private void createRecipient() {
        SaveRecipientCommand ksawery = SaveRecipientCommand.builder()
                .name("Ksawery")
                .lastName("Nowak")
                .phone("661555777")
                .street("Starej Drogi")
                .buildingNumber("11")
                .apartmentNumber("2")
                .district("Blabla")
                .city("Warszawa")
                .zipCode("01-001")
                .email("ksawer@gmail.com")
                .build();
        recipientUseCase.addRecipient(ksawery);
    }

    private void placeOrder() {

        PlaceOrderItem placeOrderItem1 = new PlaceOrderItem(2L, 2);
        PlaceOrderItem placeOrderItem2 = new PlaceOrderItem(11L, 1);
        PlaceOrderItem placeOrderItem3 = new PlaceOrderItem(1L, 3);

        PlaceOrderRecipient placeOrderRecipient = PlaceOrderRecipient.builder()
                .name("Marek")
                .lastName("Kowlaski")
                .phone("113 5564 646")
                .street("Malowniczka")
                .buildingNumber("2a")
                .district("Upaupa")
                .city("Sopot")
                .zipCode("55-021")
                .email("kowalma@gmail.com")
                .build();

        PlaceOrderCommand placeOrderCommand = PlaceOrderCommand.builder()
                .itemList(List.of(placeOrderItem1, placeOrderItem2, placeOrderItem3))
                .orderStatus(OrderStatus.NEW)
                .placeOrderRecipient(placeOrderRecipient)
                .build();

        orderUseCase.placeOrder(placeOrderCommand);

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

