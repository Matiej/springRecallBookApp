package com.testaarosa.spirngRecallBookApp.catalog.application.port;

import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

@Value
@Builder(builderMethodName = "hiddenBuilder")
public class UpdateBookCommand {
    Long id;
    String title;
    String author;
    Integer year;

    public Book updateBookFields(Book book) {
        if (!StringUtils.isBlank(title)) {
            book.setTitle(title);
        }
        if (!StringUtils.isBlank(author)) {
            book.setAuthor(author);
        }
        if (year != null && year > 0) {
            book.setYear(year);
        }
        return book;
    }

    public static UpdateBookCommandBuilder builder(Long id) {
        return hiddenBuilder().id(id);
    }
}
