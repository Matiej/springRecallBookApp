package com.testaarosa.springRecallBookApp.catalog.application;

import com.testaarosa.springRecallBookApp.catalog.application.port.AuthorUseCase;
import com.testaarosa.springRecallBookApp.catalog.controller.AuthorQueryCommand;
import com.testaarosa.springRecallBookApp.catalog.dataBase.AuthorJpaRepository;
import com.testaarosa.springRecallBookApp.catalog.domain.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService implements AuthorUseCase {
    private final AuthorJpaRepository repository;

    @Override
    public List< Author> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Author> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Author> findAllByParams(AuthorQueryCommand authorQuery) {
       return repository.findAllByAllParams(authorQuery.getName(), authorQuery.getLastName(), authorQuery.getYearOfBirth(), authorQuery.getLimit());
    }
}
