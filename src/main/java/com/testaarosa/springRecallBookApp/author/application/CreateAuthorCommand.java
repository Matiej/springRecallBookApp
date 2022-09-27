package com.testaarosa.springRecallBookApp.author.application;

import com.testaarosa.springRecallBookApp.author.domain.Author;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CreateAuthorCommand {
    String name;
    String lastName;
    Integer yearOfBirth;

    public Author toAuthor() {
        return new Author(name, lastName, yearOfBirth);
    }

    @Override
    public String toString() {
        return "CreateAuthorCommand{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", yearOfBirth=" + yearOfBirth +
                '}';
    }
}
