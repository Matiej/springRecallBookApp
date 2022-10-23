package com.testaarosa.springRecallBookApp.recipient.application.port;

import com.testaarosa.springRecallBookApp.recipient.application.RecipientResponse;
import com.testaarosa.springRecallBookApp.recipient.application.SaveRecipientCommand;
import com.testaarosa.springRecallBookApp.recipient.application.UpdateRecipientCommand;
import com.testaarosa.springRecallBookApp.recipient.controller.RecipientQueryCommand;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;

import java.util.List;
import java.util.Optional;

public interface RecipientUseCase {

    Recipient addRecipient(SaveRecipientCommand command);

    Optional<Recipient> findById(Long id);

    RecipientResponse updateRecipient(UpdateRecipientCommand command);

    Optional<Recipient> findOneByEmail(String email);

    List<Recipient> findAll();

    void removeRecipientById(Long id);

    List<Recipient> finaAllByParams(RecipientQueryCommand build);
}
