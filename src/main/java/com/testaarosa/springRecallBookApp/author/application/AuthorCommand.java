package com.testaarosa.springRecallBookApp.author.application;

import com.testaarosa.springRecallBookApp.author.domain.Author;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthorCommand {
    String name;
    String lastName;
    Integer yearOfBirth;

    public Author toAuthor() {
        return new Author(name, lastName, yearOfBirth);
    }
}
