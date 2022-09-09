package com.testaarosa.springRecallBookApp.order.domain;

import java.util.List;
import java.util.Optional;

public interface RecipientRepository {
    Recipient save(Recipient recipient);
    Optional<Recipient> findById(Long id);

}
