package com.testaarosa.springRecallBookApp.recipient.application;

import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.recipient.controller.RecipientQueryCommand;
import com.testaarosa.springRecallBookApp.recipient.dataBase.RecipientJpaRepository;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.security.UserSecurity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class RecipientService implements RecipientUseCase {
    private final RecipientJpaRepository repository;
    private final UserSecurity userSecurity;

    @Override
    public Recipient addRecipient(SaveRecipientCommand command) {
        repository.findByEmailContainingIgnoreCase(command.getEmail())
                .ifPresent(s-> {
                    throw new IllegalArgumentException("The user with email: " + command.getEmail() +
                            " is already in data base.");
                });
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
                    UserDetails user = command.getUser();
                    if (!userSecurity.isOwnerOrAdmin(recipient.getEmail(), user)) {
                        String errorMessage = "Unauthorized action for user: " + user.getUsername();
                        return RecipientResponse.FAILURE(errorMessage, recipient.getId(), RecipientResponse.RecipientErrorStatus.FORBIDDEN);
                    }
                    Recipient update = repository.save(command.updateRecipientFields(recipient));
                    return RecipientResponse.SUCCESS(update.getId());
                }).orElseGet(() -> RecipientResponse.FAILURE("No recipient to update found for ID: " + command.getId(), command.getId(), RecipientResponse.RecipientErrorStatus.NOT_FOUND));
    }

    @Override
    public Optional<Recipient> findOneByEmail(String email) {
        return repository.findByEmailContainingIgnoreCase(email);
    }

    @Override
    public List<Recipient> findAll() {
        return repository.findAll();
    }

    @Override
    public void removeRecipientById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Recipient> finaAllByParams(RecipientQueryCommand command) {
        String name = command.getName();
        String lastName = command.getLastName();
        String zipCode = command.getZipCode();
        int limit = command.getLimit();
        if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(lastName) && StringUtils.isNotEmpty(zipCode)) {
            return repository.findAllByNameAndLastNameAndZipCode(name, lastName, zipCode, limit);
        } else if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(lastName)) {
            return repository.findaAllByNameAndLastName(name, lastName, limit);

        } else if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(zipCode)) {
            return repository.findAllByNameAndZipCode(name, zipCode, limit);

        } else if (StringUtils.isNotEmpty(lastName) && StringUtils.isNotEmpty(zipCode)) {
            return repository.findAllByLastNameAndZipCode(lastName, zipCode, limit);

        } else if (StringUtils.isNotEmpty(name)) {
            return repository.findAllByNameContainingIgnoreCase(name, Pageable.ofSize(limit));

        } else if (StringUtils.isNotEmpty(lastName)) {
            return repository.findAllByLastNameContainingIgnoreCase(lastName, Pageable.ofSize(limit));

        } else if (StringUtils.isNotEmpty(zipCode)) {
            return repository.findAllByRecipientAddress_ZipCodeContainingIgnoreCase(zipCode, Pageable.ofSize(limit));

        } else if(limit > 0) {
            return repository.findAll(Pageable.ofSize(limit)).stream().collect(Collectors.toList());

        } else {
            return repository.findAll();
        }
    }
}
