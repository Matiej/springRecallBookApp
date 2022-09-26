package com.testaarosa.springRecallBookApp.author.application;

import com.testaarosa.springRecallBookApp.author.domain.Author;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CreateAuthorCommand {
    private String name;
    private String lastName;
    private Integer yearOfBirth;

    public Author toAuthor() {
        return new Author(name, lastName, yearOfBirth);
    }

}
