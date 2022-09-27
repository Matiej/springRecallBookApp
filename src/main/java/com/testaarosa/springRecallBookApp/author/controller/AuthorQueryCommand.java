package com.testaarosa.springRecallBookApp.author.controller;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class AuthorQueryCommand {
    String name;
    String lastName;
    Integer yearOfBirth;
    int limit;
}
