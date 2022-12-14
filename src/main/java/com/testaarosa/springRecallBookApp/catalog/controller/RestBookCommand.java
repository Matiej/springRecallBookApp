package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.author.controller.UpdateAuthorCommandGroup;
import com.testaarosa.springRecallBookApp.catalog.application.CreateBookCommand;
import com.testaarosa.springRecallBookApp.catalog.application.UpdateBookCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.ValidationException;
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

    private Set<Long> authors;

    @NotNull(groups = CreateBookCommandGroup.class, message = "available filed can't be null")
    @PositiveOrZero(groups = {CreateBookCommandGroup.class, UpdateAuthorCommandGroup.class}, message = "available filed must be positive or zero")
    private Long available;

    CreateBookCommand toCreateBookCommand() {
        validateAuthorsIds(authors);
        return CreateBookCommand.builder()
                .title(title)
                .year(year)
                .price(price)
                .authors(authors)
                .availAble(available)
                .build();
    }

    UpdateBookCommand toUpdateBookCommand(Long id) {
        validateAuthorsIds(authors);
        return UpdateBookCommand.builder(id)
                .title(title)
                .authors(authors)
                .year(year)
                .price(price)
                .available(available)
                .build();
    }

    private void validateAuthorsIds(Set<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) {
            throw new ValidationException("Book needs any author!");
        }
    }


}
