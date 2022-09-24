package com.testaarosa.springRecallBookApp.recipient.application.port;

import com.testaarosa.springRecallBookApp.recipient.controller.RecipientQueryCommand;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface RecipientUseCase {

     Recipient addRecipient(SaveRecipientCommand command);

    Optional<Recipient> findById(Long id);

    RecipientResponse updateRecipient(UpdateRecipientCommand command);

    Optional<Recipient> getAllRecipientsByEmail(String email);

    List<Recipient> findAll();

    void removeRecipientById(Long id);

    List<Recipient> finaAllByParams(RecipientQueryCommand build);
}
