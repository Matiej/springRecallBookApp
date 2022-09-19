package com.testaarosa.springRecallBookApp.author.application.port;

import com.testaarosa.springRecallBookApp.author.controller.AuthorQueryCommand;
import com.testaarosa.springRecallBookApp.author.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorUseCase {
    List<Author> findAll();
    Optional<Author> findById(Long id);
    List<Author> findAllByParams(AuthorQueryCommand authorQuery);
}


