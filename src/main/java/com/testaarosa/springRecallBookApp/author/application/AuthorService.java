package com.testaarosa.springRecallBookApp.author.application;

import com.testaarosa.springRecallBookApp.author.application.port.AuthorUseCase;
import com.testaarosa.springRecallBookApp.author.application.port.CreateAuthorCommand;
import com.testaarosa.springRecallBookApp.author.application.port.UpdateAuthorCommand;
import com.testaarosa.springRecallBookApp.author.application.port.UpdatedAuthorResponse;
import com.testaarosa.springRecallBookApp.author.controller.AuthorQueryCommand;
import com.testaarosa.springRecallBookApp.author.dataBase.AuthorJpaRepository;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService implements AuthorUseCase {
    private final AuthorJpaRepository repository;

    @Override
    public List<Author> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Author> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Author> findAllByParams(AuthorQueryCommand authorQuery) {
        Optional<String> name = authorQuery.getName();
        Optional<String> lastName = authorQuery.getLastName();
        Optional<Integer> yearOfBirth = authorQuery.getYearOfBirth();
        int limit = authorQuery.getLimit();
        if (name.isPresent() && lastName.isPresent() && yearOfBirth.isPresent()) {
            return repository.findAllByAllParamsLimited(name.get(), lastName.get(), yearOfBirth.get(), limit);
        } else if (name.isPresent() && lastName.isPresent()) {
            return repository.findAllByNameAndLastNameLimited(name.get(), lastName.get(), limit);
        } else if (name.isPresent() && yearOfBirth.isPresent()) {
            return repository.finAllByNameAndYearOfBirthLimited(name.get(), yearOfBirth.get(), limit);
        } else if (yearOfBirth.isPresent() && lastName.isPresent()) {
            return repository.findAllByYearOfBirthAndLastNameLimited(yearOfBirth.get(), lastName.get(), limit);
        } else if (name.isPresent()) {
            return repository.findAllByName(name.get(), limit);
        } else if (lastName.isPresent()) {
            return repository.findAllByLastName(lastName.get(), limit);
        } else if (yearOfBirth.isPresent()) {
            return repository.findAllByYearOfBirth(yearOfBirth.get(), limit);
        } else {
            return repository.findAllLimited(limit);
        }
    }

    @Override
    public Author addAuthor(CreateAuthorCommand command) {
        return repository.save(command.toAuthor());
    }

    @Override
    public UpdatedAuthorResponse updateAuthor(UpdateAuthorCommand command) {
        return repository.findById(command.getId())
                .map(authorToUpdate -> {
                    Author updateAuthor = updateAuthorFields(command, authorToUpdate);
                    repository.save(updateAuthor);
                    return UpdatedAuthorResponse.SUCCESS;
                }).orElseGet(() -> UpdatedAuthorResponse.FAILURE(List.of("Author not found for update with ID: " + command.getId())));
    }

    private Author updateAuthorFields(UpdateAuthorCommand command, Author author) {
        if (StringUtils.isNoneBlank(command.getName())) {
            author.setName(command.getName());
        }
        if (StringUtils.isNoneBlank(command.getLastName())) {
            author.setLastName(command.getLastName());
        }
        if (command.getYearOfBirth() != null && command.getYearOfBirth() > 0) {
            author.setYearOfBirth(command.getYearOfBirth());
        }
        return author;
    }

    @Override
    public void removeById(Long id, Boolean isForceDelete) {
        repository.findById(id).ifPresent(author -> {
            if (null != author.getLinkedBooks() && author.getLinkedBooks().size() > 0
                    && !isForceDelete) {
                throw new IllegalArgumentException("Can't delete author id: '" + id +
                        "' because is assigned to " + "'" + author.getLinkedBooks().size() + "' books");
            } else {
                author.removeAllBooks();
                repository.deleteById(id);
            }
        });
    }
}
