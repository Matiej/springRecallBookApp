package com.testaarosa.spirngRecallBookApp.catalog.application.port;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookCommand {
    @NotBlank(message = "Field can't be blank, empty or null")
    private String title;

    @NotBlank(message = "Field can't be blank, empty or null")
    private String author;

    @NotNull(message = "Filed can't be null")
    private Integer year;

    @NotNull
    @DecimalMin(value = "0.00", message = "Price value can't be negative, min price value is 0.00")
    private BigDecimal price;
}
