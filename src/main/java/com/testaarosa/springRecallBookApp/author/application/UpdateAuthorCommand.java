package com.testaarosa.springRecallBookApp.author.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderMethodName = "hiddenBuilder")
public class UpdateAuthorCommand {
    Long id;
    String name;
    String lastName;
    Integer yearOfBirth;

    public static UpdateAuthorCommandBuilder builder(Long id) {
        return hiddenBuilder().id(id);
    }

}
