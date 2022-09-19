package com.testaarosa.springRecallBookApp.author.controller;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class AuthorQueryCommand {
    Optional<String> name;
    Optional<String> lastName;
    Optional<Integer> yearOfBirth;
    int limit;
}
