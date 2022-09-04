package com.testaarosa.spirngRecallBookApp.catalog.controller;

import com.testaarosa.spirngRecallBookApp.catalog.application.port.CreateBookCommand;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.CreateBookCommandGroup;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.UpdateBookCommand;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.UpdateBookCommandGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
class RestBookCommand {
    @NotBlank(groups = CreateBookCommandGroup.class, message = "Title field can't be blank, empty or null")
    private String title;

    @NotBlank(groups = CreateBookCommandGroup.class, message = "Author field can't be blank, empty or null")
    private String author;

    @NotNull(groups = CreateBookCommandGroup.class, message = "Year filed can't be null")
    private Integer year;

    @NotNull(groups = CreateBookCommandGroup.class, message = "price filed can't be null")
    @DecimalMin(groups = {CreateBookCommandGroup.class, UpdateBookCommandGroup.class}, value = "0.00", message = "Price value can't be negative, min price value is 0.00")
    private BigDecimal price;

    CreateBookCommand toCreateBookCommand() {
        return CreateBookCommand.builder().title(title).author(author).year(year).price(price).build();
    }

    UpdateBookCommand toUpdateBookCommand(Long id) {
        return UpdateBookCommand.builder(id).title(title).author(author).year(year).price(price).build();
    }

}
