package com.testaarosa.springRecallBookApp.author.application;

import com.testaarosa.springRecallBookApp.author.application.port.AuthorUseCase;
import com.testaarosa.springRecallBookApp.author.controller.AuthorQueryCommand;
import com.testaarosa.springRecallBookApp.author.dataBase.AuthorJpaRepository;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import lombok.RequiredArgsConstructor;
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
        } else if(yearOfBirth.isPresent()) {
            return repository.findAllByYearOfBirth(yearOfBirth.get(), limit);
        } else {
            return repository.findAllLimited(limit);
        }
    }

}
