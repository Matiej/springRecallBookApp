package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.author.controller.UpdateAuthorCommandGroup;
import com.testaarosa.springRecallBookApp.catalog.application.port.CreateBookCommand;
import com.testaarosa.springRecallBookApp.catalog.application.port.UpdateBookCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
class RestBookCommand {
    @NotBlank(groups = CreateBookCommandGroup.class, message = "Title field can't be blank, empty or null")
    private String title;
    @NotNull(groups = CreateBookCommandGroup.class, message = "Year filed can't be null")
    @Digits(groups = {CreateBookCommandGroup.class, UpdateAuthorCommandGroup.class}, integer = 4,
            message = "yearOfBirth filed expects 4 digit value", fraction = 0)
    private Integer year;
    @NotNull(groups = CreateBookCommandGroup.class, message = "price filed can't be null")
    @DecimalMin(groups = {CreateBookCommandGroup.class, UpdateBookCommandGroup.class}, value = "0.00", message = "Price value can't be negative, min price value is 0.00")
    private BigDecimal price;
    @NotEmpty(groups = CreateBookCommand.class, message = "Book needs any author!")
    private Set<Long> authors;
    @NotNull(groups = CreateBookCommandGroup.class, message = "available filed can't be null")
    @PositiveOrZero(groups = {CreateBookCommandGroup.class, UpdateAuthorCommandGroup.class})
    private Long available;

    CreateBookCommand toCreateBookCommand() {
        return CreateBookCommand.builder()
                .title(title)
                .year(year)
                .price(price)
                .authors(authors)
                .availAble(available)
                .build();
    }

    UpdateBookCommand toUpdateBookCommand(Long id) {
        return UpdateBookCommand.builder(id)
                .title(title)
                .authors(authors)
                .year(year)
                .price(price)
                .available(available)
                .build();
    }


}
