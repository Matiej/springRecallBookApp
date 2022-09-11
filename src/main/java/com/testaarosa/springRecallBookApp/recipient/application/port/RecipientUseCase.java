package com.testaarosa.springRecallBookApp.recipient.application.port;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;

import java.util.List;
import java.util.Optional;

public interface RecipientUseCase {

     Recipient addRecipient(SaveRecipientCommand command);

    Optional<Recipient> findById(Long id);

    RecipientResponse updateRecipient(UpdateRecipientCommand command);

    List<Recipient> getAllRecipientsByEmail(String email);

    List<Recipient> getAll();

    void removeRecipientById(Long id);
}
