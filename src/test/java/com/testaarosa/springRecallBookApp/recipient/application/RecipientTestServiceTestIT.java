package com.testaarosa.springRecallBookApp.recipient.application;

import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.recipient.RecipientTestBase;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.recipient.controller.RecipientQueryCommand;
import com.testaarosa.springRecallBookApp.recipient.dataBase.RecipientJpaRepository;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        SaveRecipientCommand saveRecipientCommand = prepareSaveRecipientCommandJan();
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
        SaveRecipientCommand command = prepareSaveRecipientCommandJan();
        String expectedErrorMessage = "The user with email: " + command.getEmail() +
                " is already in data base.";
        Recipient recipient = recipientUseCase.addRecipient(command);
        Recipient recipientFromDb = recipientJpaRepository.findById(recipient.getId()).get();
        assertRecipientFields(recipientFromDb, command);

        //when
        Throwable throwable = catchThrowable(() -> recipientUseCase.addRecipient(command));

        //then
        then(throwable).as("An IllegalArgumentException should be thrown if limit is less then 1")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    @Test
    @DisplayName("Should update recipient, method updateRecipient(Recipient recipient).")
    void shouldUpdateRecipient() {
        //given
        SaveRecipientCommand saveRecipientCommand = prepareSaveRecipientCommandJan();
        Recipient recipient = recipientUseCase.addRecipient(saveRecipientCommand);
        UserDetails user = prepareUser(recipient.getEmail());
        UpdateRecipientCommand updateRecipientCommand = prepareUpdateRecipientCommand(recipient, recipient.getId(), user);
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
        SaveRecipientCommand saveRecipientCommand = prepareSaveRecipientCommandJan();
        Recipient recipient = recipientUseCase.addRecipient(saveRecipientCommand);
        UserDetails userDetails = prepareUser(recipient.getEmail());
        UpdateRecipientCommand updateRecipientCommand = prepareUpdateRecipientCommand(recipient, 111L, userDetails);
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

    @Test
    @DisplayName("Should NOT update recipient, wrong user, method updateRecipient(Recipient recipient).")
    void shouldNotUpdateRecipientFailureWrongUser() {
        //given
        SaveRecipientCommand saveRecipientCommand = prepareSaveRecipientCommandJan();
        Recipient recipient = recipientUseCase.addRecipient(saveRecipientCommand);
        UserDetails user = prepareUser("anyuser@anyuser.com");
        UpdateRecipientCommand updateRecipientCommand = prepareUpdateRecipientCommand(recipient, recipient.getId(), user);
        //when

        RecipientResponse response = recipientUseCase.updateRecipient(updateRecipientCommand);

        //then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("Unauthorized action for user: " + user.getUsername(), response.getError());
        Recipient recipientFromDb = recipientJpaRepository.findById(recipient.getId()).get();
        assertNotNull(recipientFromDb);
        assertRecipientFields(recipientFromDb, saveRecipientCommand);
    }

    @Test
    @DisplayName("Should find recipient by email, method findOneByEmail(String email).")
    void shouldFindRecipientByEmail() {
        //given
        SaveRecipientCommand saveRecipientCommand = prepareSaveRecipientCommandJan();
        Recipient recipient = recipientUseCase.addRecipient(saveRecipientCommand);
        //when

        Optional<Recipient> response = recipientUseCase.findOneByEmail(recipient.getEmail());

        //then
        assertNotNull(response);
        assertFalse(response.isEmpty());
        Recipient receivedRecipient = response.get();
        assertEquals(recipient.getId(), receivedRecipient.getId());
        assertEquals(recipient.getUUID(), receivedRecipient.getUUID());
        assertEquals(recipient.getEmail(), receivedRecipient.getEmail());
    }

    @Test
    @DisplayName("Should NOT find recipient by email, method findOneByEmail(String email).")
    void shouldNotFindRecipientByEmail() {
        //given
        SaveRecipientCommand saveRecipientCommand = prepareSaveRecipientCommandJan();
        Recipient recipient = recipientUseCase.addRecipient(saveRecipientCommand);

        //when
        Optional<Recipient> response = recipientUseCase.findOneByEmail("anyother@email.com");

        //then
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("Should find all 3 recipients by name, method finaAllByParams(RecipientQueryCommand command).")
    void shouldFindRecipientByName() {
        //given
        SaveRecipientCommand janCommand = prepareSaveRecipientCommandJan();
        recipientUseCase.addRecipient(janCommand);
        SaveRecipientCommand joannaCommand = prepareSaveRecipientCommandAndrea("jok@joanna.de", "Joanna");
        Recipient joanna = recipientUseCase.addRecipient(joannaCommand);
        SaveRecipientCommand annaCommand = prepareSaveRecipientCommandAndrea("jok@anna.de", "Anna");
        Recipient anna = recipientUseCase.addRecipient(annaCommand);
        SaveRecipientCommand hannaCommand = prepareSaveRecipientCommandAndrea("jok@hanna.de", "Hanna");
        Recipient hanna = recipientUseCase.addRecipient(hannaCommand);

        //when
        RecipientQueryCommand recipientQueryCommand = RecipientQueryCommand.builder()
                .name("anna")
                .limit(255)
                .build();
        List<Recipient> recipients = recipientUseCase.finaAllByParams(recipientQueryCommand);

        //then
        List<Recipient> recipientsToSave = Arrays.asList(anna, joanna, hanna);
        assertNotNull(recipients);
        assertEquals(3, recipients.size());
        assertThat(recipientsToSave).hasSameElementsAs(recipients);
    }

    @Test
    @DisplayName("Should find recipient by name and lastName, method finaAllByParams(RecipientQueryCommand command).")
    void shouldFindRecipientByNameAndLastName() {
        //given
        SaveRecipientCommand janCommand = prepareSaveRecipientCommandJan();
        Recipient jan = recipientUseCase.addRecipient(janCommand);
        SaveRecipientCommand andreaCommand = prepareSaveRecipientCommandAndrea("jok@andrea.de", "Andrea");
        Recipient andrea = recipientUseCase.addRecipient(andreaCommand);

        //when
        RecipientQueryCommand recipientQueryCommand = RecipientQueryCommand.builder()
                .name("Andr")
                .lastName("Jok")
                .limit(1)
                .build();
        List<Recipient> recipients = recipientUseCase.finaAllByParams(recipientQueryCommand);

        //then
        assertNotNull(recipients);
        assertEquals(1, recipients.size());
        Recipient recipient = recipients.get(0);
        assertRecipientFields(recipient, andreaCommand);
    }

    @Test
    @DisplayName("Should find recipient by name and lastName and zipcode, method finaAllByParams(RecipientQueryCommand command).")
    void shouldFindRecipientByNameAndLastNameAndZipCode() {
        //given
        SaveRecipientCommand janCommand = prepareSaveRecipientCommandJan();
        Recipient jan = recipientUseCase.addRecipient(janCommand);
        SaveRecipientCommand andreaCommand = prepareSaveRecipientCommandAndrea("jok@andrea.de", "Andrea");
        Recipient andrea = recipientUseCase.addRecipient(andreaCommand);

        //when
        RecipientQueryCommand recipientQueryCommand = RecipientQueryCommand.builder()
                .name("Andr")
                .lastName("Jok")
                .zipCode("001PS")
                .limit(255)
                .build();
        List<Recipient> recipients = recipientUseCase.finaAllByParams(recipientQueryCommand);

        //then
        assertNotNull(recipients);
        assertEquals(1, recipients.size());
        Recipient recipient = recipients.get(0);
        assertRecipientFields(recipient, andreaCommand);
    }

    @Test
    @DisplayName("Should find recipient by name and zipcode, method finaAllByParams(RecipientQueryCommand command).")
    void shouldFindRecipientByNameAndZipCode() {
        //given
        SaveRecipientCommand janCommand = prepareSaveRecipientCommandJan();
        Recipient jan = recipientUseCase.addRecipient(janCommand);
        SaveRecipientCommand andreaCommand = prepareSaveRecipientCommandAndrea("jok@andrea.de", "Andrea");
        Recipient andrea = recipientUseCase.addRecipient(andreaCommand);

        //when
        RecipientQueryCommand recipientQueryCommand = RecipientQueryCommand.builder()
                .name("Andr")
                .zipCode("001PS")
                .limit(255)
                .build();
        List<Recipient> recipients = recipientUseCase.finaAllByParams(recipientQueryCommand);

        //then
        assertNotNull(recipients);
        assertEquals(1, recipients.size());
        Recipient recipient = recipients.get(0);
        assertRecipientFields(recipient, andreaCommand);
    }

    @Test
    @DisplayName("Should find recipient by lanstName and zipcode, method finaAllByParams(RecipientQueryCommand command).")
    void shouldFindRecipientByLastNameAndZipCode() {
        //given
        SaveRecipientCommand janCommand = prepareSaveRecipientCommandJan();
        Recipient jan = recipientUseCase.addRecipient(janCommand);
        SaveRecipientCommand andreaCommand = prepareSaveRecipientCommandAndrea("jok@andrea.de", "Andrea");
        Recipient andrea = recipientUseCase.addRecipient(andreaCommand);

        //when
        RecipientQueryCommand recipientQueryCommand = RecipientQueryCommand.builder()
                .lastName("Jok")
                .zipCode("001PS")
                .limit(255)
                .build();
        List<Recipient> recipients = recipientUseCase.finaAllByParams(recipientQueryCommand);

        //then
        assertNotNull(recipients);
        assertEquals(1, recipients.size());
        Recipient recipient = recipients.get(0);
        assertRecipientFields(recipient, andreaCommand);
    }

    @Test
    @DisplayName("Should find 2 recipients by lastName uppercase (limit=2), method finaAllByParams(RecipientQueryCommand command).")
    void shouldFindRecipientByLastNameUpperCase() {
        //given
        SaveRecipientCommand janCommand = prepareSaveRecipientCommandJan();
        recipientUseCase.addRecipient(janCommand);
        SaveRecipientCommand joannaCommand = prepareSaveRecipientCommandAndrea("jok@joanna.de", "Joanna");
        Recipient joanna = recipientUseCase.addRecipient(joannaCommand);
        SaveRecipientCommand annaCommand = prepareSaveRecipientCommandAndrea("jok@anna.de", "Anna");
        Recipient anna = recipientUseCase.addRecipient(annaCommand);
        SaveRecipientCommand hannaCommand = prepareSaveRecipientCommandAndrea("jok@hanna.de", "Hanna");
        Recipient hanna = recipientUseCase.addRecipient(hannaCommand);

        //when
        RecipientQueryCommand recipientQueryCommand = RecipientQueryCommand.builder()
                .lastName("JOK")
                .limit(2)
                .build();
        List<Recipient> recipients = recipientUseCase.finaAllByParams(recipientQueryCommand);
        //then
        List<Recipient> recipientsToSave = Arrays.asList(anna, joanna, hanna);
        assertNotNull(recipients);
        assertEquals(2, recipients.size());
        assertTrue(CollectionUtils.containsAny(recipientsToSave, recipients));
    }

    @Test
    @DisplayName("Should find 1 recipients by zipcode (limit=1), method finaAllByParams(RecipientQueryCommand command).")
    void shouldFindRecipientByZipCode() {
        //given
        SaveRecipientCommand janCommand = prepareSaveRecipientCommandJan();
        recipientUseCase.addRecipient(janCommand);
        SaveRecipientCommand joannaCommand = prepareSaveRecipientCommandAndrea("jok@joanna.de", "Joanna");
        Recipient joanna = recipientUseCase.addRecipient(joannaCommand);
        SaveRecipientCommand annaCommand = prepareSaveRecipientCommandAndrea("jok@anna.de", "Anna");
        Recipient anna = recipientUseCase.addRecipient(annaCommand);
        SaveRecipientCommand hannaCommand = prepareSaveRecipientCommandAndrea("jok@hanna.de", "Hanna");
        Recipient hanna = recipientUseCase.addRecipient(hannaCommand);

        //when
        RecipientQueryCommand recipientQueryCommand = RecipientQueryCommand.builder()
                .zipCode("001PS")
                .limit(1)
                .build();
        List<Recipient> recipients = recipientUseCase.finaAllByParams(recipientQueryCommand);

        //then
        List<Recipient> recipientsToSave = Arrays.asList(anna, joanna, hanna);
        assertNotNull(recipients);
        assertEquals(1, recipients.size());
        assertTrue(CollectionUtils.containsAny(recipientsToSave, recipients));
    }

    @Test
    @DisplayName("Should find ALL recipients no params, method finaAllByParams(RecipientQueryCommand command).")
    void shouldFindAllRecipientNoParams() {
        //given
        SaveRecipientCommand janCommand = prepareSaveRecipientCommandJan();
        Recipient jan = recipientUseCase.addRecipient(janCommand);
        SaveRecipientCommand joannaCommand = prepareSaveRecipientCommandAndrea("jok@joanna.de", "Joanna");
        Recipient joanna = recipientUseCase.addRecipient(joannaCommand);
        SaveRecipientCommand annaCommand = prepareSaveRecipientCommandAndrea("jok@anna.de", "Anna");
        Recipient anna = recipientUseCase.addRecipient(annaCommand);
        SaveRecipientCommand hannaCommand = prepareSaveRecipientCommandAndrea("jok@hanna.de", "Hanna");
        Recipient hanna = recipientUseCase.addRecipient(hannaCommand);

        //when
        RecipientQueryCommand recipientQueryCommand = RecipientQueryCommand.builder()
                .build();
        List<Recipient> recipients = recipientUseCase.finaAllByParams(recipientQueryCommand);

        //then
        List<Recipient> recipientsToSave = Arrays.asList(anna, joanna, hanna, jan);
        assertNotNull(recipients);
        assertEquals(4, recipients.size());
        assertTrue(CollectionUtils.containsAny(recipientsToSave, recipients));
    }

    @Test
    @DisplayName("Should find 3 recipients limit=3, method finaAllByParams(RecipientQueryCommand command).")
    void shouldFindAllRecipientLimit3() {
        //given
        SaveRecipientCommand janCommand = prepareSaveRecipientCommandJan();
        Recipient jan = recipientUseCase.addRecipient(janCommand);
        SaveRecipientCommand joannaCommand = prepareSaveRecipientCommandAndrea("jok@joanna.de", "Joanna");
        Recipient joanna = recipientUseCase.addRecipient(joannaCommand);
        SaveRecipientCommand annaCommand = prepareSaveRecipientCommandAndrea("jok@anna.de", "Anna");
        Recipient anna = recipientUseCase.addRecipient(annaCommand);
        SaveRecipientCommand hannaCommand = prepareSaveRecipientCommandAndrea("jok@hanna.de", "Hanna");
        Recipient hanna = recipientUseCase.addRecipient(hannaCommand);

        //when
        RecipientQueryCommand recipientQueryCommand = RecipientQueryCommand.builder()
                .limit(3)
                .build();
        List<Recipient> recipients = recipientUseCase.finaAllByParams(recipientQueryCommand);

        //then
        List<Recipient> recipientsToSave = Arrays.asList(anna, joanna, hanna, jan);
        assertNotNull(recipients);
        assertEquals(3, recipients.size());
        assertTrue(CollectionUtils.containsAny(recipientsToSave, recipients));
    }

    @Test
    @DisplayName("Should NOT find recipients limit=-1, throws IllegalArgumentException, method finaAllByParams(RecipientQueryCommand command).")
    void shouldNotFindAllRecipientIllegalArgumentException() {
        //given
        SaveRecipientCommand janCommand = prepareSaveRecipientCommandJan();
        recipientUseCase.addRecipient(janCommand);
        String expectedErrorMessage = "Page size must not be less than one";

        //when
        RecipientQueryCommand recipientQueryCommand = RecipientQueryCommand.builder()
                .limit(-1)
                .zipCode("001PS")
                .build();
        Throwable throwable = catchThrowable(() -> recipientUseCase.finaAllByParams(recipientQueryCommand));


//        then
        then(throwable).as("An IllegalArgumentException should be thrown if limit is less then 1")
                .isInstanceOf(IllegalArgumentException.class)
                .as("Check that message equal expected message")
                .hasMessageContaining(expectedErrorMessage);
    }

    private void assertRecipientFields(Recipient recipient, RecipientCommand command) {
        assertAll("Assert recipient fields",
                () -> assertEquals(recipient.getName(), command.getName()),
                () -> assertEquals(recipient.getLastName(), command.getLastName()),
                () -> assertEquals(recipient.getPhone(), command.getPhone()),
                () -> assertEquals(recipient.getEmail(), recipient.getEmail())
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

    private UserDetails prepareUser(String name) {
        return new User(name, "test123", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private UpdateRecipientCommand prepareUpdateRecipientCommand(Recipient recipient, Long id, UserDetails user) {
        RecipientAddress recipientAddress = recipient.getRecipientAddress();
        return UpdateRecipientCommand.hiddenBuilder()
                .id(id)
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
                .user(user)
                .build();
    }

    private String updateFiled(String contentToUpdate) {
        return contentToUpdate + "_updated";
    }

    private SaveRecipientCommand prepareSaveRecipientCommandJan() {
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

    private SaveRecipientCommand prepareSaveRecipientCommandAndrea(String email, String name) {
        return SaveRecipientCommand.builder()
                .name(name)
                .lastName("Jokoplusu")
                .phone("111 555 444")
                .email(email)
                .street("White")
                .buildingNumber("121")
                .apartmentNumber("2a")
                .district("huge")
                .city("Alasca")
                .zipCode("001PS")
                .build();
    }


}