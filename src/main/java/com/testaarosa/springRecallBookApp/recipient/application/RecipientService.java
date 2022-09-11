package com.testaarosa.springRecallBookApp.recipient.application;

import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientResponse;
import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.recipient.application.port.SaveRecipientCommand;
import com.testaarosa.springRecallBookApp.recipient.application.port.UpdateRecipientCommand;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public RecipientResponse updateRecipient(UpdateRecipientCommand command) {
        return findById(command.getId())
                .map(recipient -> {
                    Recipient update = repository.save(command.updateRecipientFields(recipient));
                    return RecipientResponse.SUCCESS(update.getId());
                }).orElseGet(() -> RecipientResponse.FAILURE(List.of("No recipient to update found for ID: " + command.getId())));
    }

    @Override
    public List<Recipient> getAllRecipientsByEmail(String email) {
        return repository.getAllRecipientsByEmail(email);
    }

    @Override
    public List<Recipient> getAll() {
        return repository.getAll();
    }

    @Override
    public void removeRecipientById(Long id) {
        repository.delete(id);
    }
}
