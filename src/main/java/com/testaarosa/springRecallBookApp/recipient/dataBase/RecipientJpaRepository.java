package com.testaarosa.springRecallBookApp.recipient.dataBase;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipientJpaRepository extends JpaRepository<Recipient, Long> {
    List<Recipient> getAllRecipientsByEmail(String email);
}
