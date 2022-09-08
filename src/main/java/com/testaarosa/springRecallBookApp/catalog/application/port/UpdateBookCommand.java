package com.testaarosa.springRecallBookApp.catalog.application.port;

import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Value
@Builder(builderMethodName = "hiddenBuilder")
public class UpdateBookCommand {
    Long id;
    String title;
    String author;
    Integer year;
    BigDecimal price;

    public Book updateBookFields(Book book) {
        if (StringUtils.isNoneBlank(title)) {
            book.setTitle(title);
        }
        if (StringUtils.isNoneBlank(author)) {
            book.setAuthor(author);
        }
        if (year != null && year > 0) {
            book.setYear(year);
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) > -1) {
            book.setPrice(price);
        }
        return book;
    }

    public static UpdateBookCommandBuilder builder(Long id) {
        return hiddenBuilder().id(id);
    }
}
