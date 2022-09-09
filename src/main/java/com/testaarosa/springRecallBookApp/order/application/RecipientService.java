package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.order.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.order.application.port.SaveRecipientCommand;
import com.testaarosa.springRecallBookApp.order.application.port.UpdateRecipientCommand;
import com.testaarosa.springRecallBookApp.order.application.port.UpdateRecipientResponse;
import com.testaarosa.springRecallBookApp.order.domain.Recipient;
import com.testaarosa.springRecallBookApp.order.domain.RecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class RecipientService implements RecipientUseCase {
    private final RecipientRepository repository;

    @Override
    public Recipient addRecipient(SaveRecipientCommand command) {
        return repository.save(command.toRecipient());
    }

    @Override
    public Optional<Recipient> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public UpdateRecipientResponse updateRecipient(UpdateRecipientCommand command) {
        return null;
    }
}
