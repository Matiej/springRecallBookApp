package com.testaarosa.springRecallBookApp.author.application.port;

import com.testaarosa.springRecallBookApp.author.application.CreateAuthorCommand;
import com.testaarosa.springRecallBookApp.author.application.UpdateAuthorCommand;
import com.testaarosa.springRecallBookApp.author.application.UpdatedAuthorResponse;
import com.testaarosa.springRecallBookApp.author.controller.AuthorQueryCommand;
import com.testaarosa.springRecallBookApp.author.domain.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorUseCase {
    List<Author> findAll();
    Optional<Author> findById(Long id);
    List<Author> findAllByParams(AuthorQueryCommand authorQuery);
    Author addAuthor(CreateAuthorCommand command);
    UpdatedAuthorResponse updateAuthor(UpdateAuthorCommand command);
    void removeById(Long id, Boolean isForceDelete);

    void save(Author author);
}


