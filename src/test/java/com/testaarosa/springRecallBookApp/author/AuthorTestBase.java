package com.testaarosa.springRecallBookApp.author;

import com.testaarosa.springRecallBookApp.BaseTest;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
public class AuthorTestBase extends BaseTest {

    protected List<Author> prepareAuthors() {
        Author a1 = new Author("James", "Nowak", 1888);
        Author a2 = new Author("Adam", "Mickiewicz", 1781);
        Author a3 = new Author("Eliza", "Orzeszkowa", 1791);
        Author a4 = new Author("Ken", "Follet", 1975);
        Author a5 = new Author("John", "Grisham", 1955);
        Author a6 = new Author("John", "Grisham", 1955);

        return List.of(a1, a2, a3, a4, a5, a6);
    }



}
