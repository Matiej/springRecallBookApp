package com.testaarosa.springRecallBookApp.catalog.application.port;

import com.testaarosa.springRecallBookApp.catalog.controller.AuthorQueryCommand;
import com.testaarosa.springRecallBookApp.catalog.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorUseCase {
    List<Author> findAll();
    Optional<Author> findById(Long id);
    List<Author> findAllByParams(AuthorQueryCommand authorQuery);
}


