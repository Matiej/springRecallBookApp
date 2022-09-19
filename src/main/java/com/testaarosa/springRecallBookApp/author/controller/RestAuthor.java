package com.testaarosa.springRecallBookApp.author.controller;

import com.testaarosa.springRecallBookApp.catalog.controller.CreateBookCommandGroup;
import com.testaarosa.springRecallBookApp.catalog.controller.UpdateBookCommandGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestAuthor {
    @NotBlank(groups = CreateBookCommandGroup.class, message = "Name field can't be blank, empty or null")
    private String name;
    @NotBlank(groups = CreateBookCommandGroup.class, message = "LastName field can't be blank, empty or null")
    private String lastName;
    @NotNull(groups = CreateBookCommandGroup.class, message = "YearOfBirth can't be null")
    @Digits(groups = {CreateBookCommandGroup.class, UpdateBookCommandGroup.class}, integer = 4,
            message = "yearOfBirth filed expects 4 digit value", fraction = 0)
    private Integer yearOfBirth;
}
