package com.testaarosa.springRecallBookApp.recipient.application;

import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.recipient.RecipientTestBase;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.recipient.dataBase.RecipientJpaRepository;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

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

    @Test
    @DisplayName("Should update recipient, method updateRecipient(Recipient recipient).")
    void shouldUpdateRecipient() {
        //given
        SaveRecipientCommand saveRecipientCommand = prepareSaveRecipientCommand();
        Recipient recipient = recipientUseCase.addRecipient(saveRecipientCommand);
        UpdateRecipientCommand updateRecipientCommand = prepareUpdateRecipientCommand(recipient, recipient.getId());
        //when

        RecipientResponse response = recipientUseCase.updateRecipient(updateRecipientCommand);

        //then
        assertNotNull(response);
        assertTrue(response.isSuccess());

        Recipient recipientFromDb = recipientJpaRepository.findById(recipient.getId()).get();
        assertNotNull(recipientFromDb);
        assertRecipientFields(recipientFromDb, updateRecipientCommand);
    }

    @Test
    @DisplayName("Should NOT update recipient, wrong ID, method updateRecipient(Recipient recipient).")
    void shouldNotUpdateRecipientFailure() {
        //given
        SaveRecipientCommand saveRecipientCommand = prepareSaveRecipientCommand();
        Recipient recipient = recipientUseCase.addRecipient(saveRecipientCommand);
        UpdateRecipientCommand updateRecipientCommand = prepareUpdateRecipientCommand(recipient, 111L);
        //when

        RecipientResponse response = recipientUseCase.updateRecipient(updateRecipientCommand);

        //then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("No recipient to update found for ID: " + 111, response.getError());
        Recipient recipientFromDb = recipientJpaRepository.findById(recipient.getId()).get();
        assertNotNull(recipientFromDb);
        assertRecipientFields(recipientFromDb, saveRecipientCommand);
    }

    private void assertRecipientFields(Recipient recipient, RecipientCommand command) {
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

    private UpdateRecipientCommand prepareUpdateRecipientCommand(Recipient recipient, Long id) {
        RecipientAddress recipientAddress = recipient.getRecipientAddress();
        return (UpdateRecipientCommand) UpdateRecipientCommand.builder(id)
                .name(updateFiled(recipient.getName()))
                .lastName(updateFiled(recipient.getLastName()))
                .phone(updateFiled(recipient.getPhone()))
                .email(updateFiled(recipient.getEmail()))
                .street(updateFiled(recipientAddress.getStreet()))
                .buildingNumber(updateFiled(recipientAddress.getBuildingNumber()))
                .apartmentNumber(updateFiled(recipientAddress.getApartmentNumber()))
                .district(updateFiled(recipientAddress.getDistrict()))
                .city(updateFiled(recipientAddress.getCity()))
                .zipCode(updateFiled(recipientAddress.getZipCode()))
                .build();
    }

    private String updateFiled(String contentToUpdate) {
        return contentToUpdate+"_updated";
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