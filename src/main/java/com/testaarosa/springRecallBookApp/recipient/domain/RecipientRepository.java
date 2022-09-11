package com.testaarosa.springRecallBookApp.recipient.domain;

import java.util.List;
import java.util.Optional;

public interface RecipientRepository {
    Recipient save(Recipient recipient);

    Optional<Recipient> findById(Long id);

    List<Recipient> getAllRecipientsByEmail(String email);

    List<Recipient> getAll();

    void delete(Long id);
}
