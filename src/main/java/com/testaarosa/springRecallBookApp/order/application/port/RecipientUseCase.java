package com.testaarosa.springRecallBookApp.order.application.port;

import com.testaarosa.springRecallBookApp.catalog.application.port.UpdateBookResponse;
import com.testaarosa.springRecallBookApp.order.domain.Recipient;

import java.util.Optional;

public interface RecipientUseCase {

    Recipient addRecipient(SaveRecipientCommand command);
    Optional<Recipient> findById(Long id);
    UpdateRecipientResponse updateRecipient(UpdateRecipientCommand command);
}
