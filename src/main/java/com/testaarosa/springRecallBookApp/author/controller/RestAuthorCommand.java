package com.testaarosa.springRecallBookApp.author.controller;

import com.testaarosa.springRecallBookApp.author.application.CreateAuthorCommand;
import com.testaarosa.springRecallBookApp.author.application.UpdateAuthorCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
class RestAuthorCommand {
    @NotBlank(groups = CreateAuthorCommandGroup.class, message = "Name field can't be blank, empty or null")
    private String name;
    @NotBlank(groups = CreateAuthorCommandGroup.class, message = "LastName field can't be blank, empty or null")
    private String lastName;
    @NotNull(groups = CreateAuthorCommandGroup.class, message = "YearOfBirth can't be null")
    @Digits(groups = {CreateAuthorCommandGroup.class, UpdateAuthorCommandGroup.class}, integer = 4,
            message = "yearOfBirth filed expects 4 digit value", fraction = 0)
    private Integer yearOfBirth;

    CreateAuthorCommand toCreateAuthorCommand() {
        return CreateAuthorCommand.builder()
                .name(name)
                .lastName(lastName)
                .yearOfBirth(yearOfBirth)
                .build();
    }

    UpdateAuthorCommand toUpdateAuthorCommand(Long id) {
        return UpdateAuthorCommand.builder(id)
                .name(name)
                .lastName(lastName)
                .yearOfBirth(yearOfBirth)
                .build();
    }
}
