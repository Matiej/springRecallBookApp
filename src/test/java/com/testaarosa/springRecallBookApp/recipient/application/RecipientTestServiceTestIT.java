package com.testaarosa.springRecallBookApp.recipient.application.port;

import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderCommand;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderItem;
import com.testaarosa.springRecallBookApp.order.application.PlaceOrderRecipient;
import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.order.domain.Delivery;
import com.testaarosa.springRecallBookApp.recipient.RecipientTestBase;
import com.testaarosa.springRecallBookApp.recipient.application.SaveRecipientCommand;
import com.testaarosa.springRecallBookApp.recipient.dataBase.RecipientJpaRepository;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource("classpath:application-test.properties")
class RecipientTestServiceTestIT extends RecipientTestBase {
    //todo - Maciek - test to finish!
    @Autowired
    private RecipientUseCase recipientUseCase;
    @Autowired
    private RecipientJpaRepository recipientJpaRepository;
    @Autowired
    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        log.info("Starting test: {}.", testInfo.getDisplayName());
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        log.info("Finished test:  {}", testInfo.getDisplayName());
    }

    @Test
    @DisplayName("Should recipient be added into DB, method addRecipient().")
    void shouldAddRecipient() {
        //given
        SaveRecipientCommand saveRecipientCommand = prepareSaveRecipientCommand();
        //when
        Recipient recipient = recipientUseCase.addRecipient(saveRecipientCommand);

        //then
        Recipient recipientFromDb = recipientJpaRepository.findById(recipient.getId()).get();
        assertNotNull(recipientFromDb);
        assertRecipientFields(recipientFromDb, saveRecipientCommand);
    }

    @Test
    @DisplayName("Should NOT add recipient with the same email address. Throws IllegalArgumentException")
    void shouldNotAddRecipientWithSameEmailIllegalArgumentException() {
        //given
        SaveRecipientCommand command = prepareSaveRecipientCommand();
        String expectedErrorMessage = "The user with email: " + command.getEmail() +
                " is already in data base.";
        Recipient recipient = recipientUseCase.addRecipient(command);
        Recipient recipientFromDb = recipientJpaRepository.findById(recipient.getId()).get();
        assertRecipientFields(recipientFromDb, command);

        //when
        Throwable throwable = catchThrowable(() -> recipientUseCase.addRecipient(command));

        //then
        then(throwable).as("An ConstraintViolationException should be thrown if limit is less then 1")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    private void assertRecipientFields(Recipient recipient, SaveRecipientCommand command) {
        assertAll("Assert recipient fields",
                () -> assertEquals(recipient.getName(), command.getName()),
                () -> assertEquals(recipient.getLastName(), command.getLastName()),
                () -> assertEquals(recipient.getPhone(), command.getPhone()),
                () -> assertEquals(recipient.getEmail(), command.getEmail())
        );
        RecipientAddress recipientAddress = recipient.getRecipientAddress();
        assertNotNull(recipientAddress);
        assertAll("Assert recipientAddress fields",
                () -> assertEquals(recipientAddress.getStreet(), command.getStreet()),
                () -> assertEquals(recipientAddress.getBuildingNumber(), command.getBuildingNumber()),
                () -> assertEquals(recipientAddress.getApartmentNumber(), command.getApartmentNumber()),
                () -> assertEquals(recipientAddress.getDistrict(), command.getDistrict()),
                () -> assertEquals(recipientAddress.getDistrict(), command.getDistrict()),
                () -> assertEquals(recipientAddress.getCity(), command.getCity()),
                () -> assertEquals(recipientAddress.getZipCode(), command.getZipCode())
        );
    }

    private SaveRecipientCommand prepareSaveRecipientCommand() {
        return SaveRecipientCommand.builder()
                .name("Jan")
                .lastName("Kowalski")
                .phone("111 555 444")
                .email("jak@kowalski.com")
                .street("Jasna")
                .buildingNumber("11")
                .apartmentNumber("21")
                .district("district")
                .city("Sopot")
                .zipCode("00-010")
                .build();
    }



}