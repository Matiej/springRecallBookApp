package com.testaarosa.springRecallBookApp.catalog;

import com.testaarosa.springRecallBookApp.BaseTest;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class CatalogTestBase extends BaseTest {

    protected List<Book> prepareBooks() {

        Book effective_java = new Book(
                "Effective Java",
                2005,
                new BigDecimal(10),
                12L);

        Book mama_mia = new Book(
                "Mama mia",
                2015,
                new BigDecimal(10),
                12L);
        return Arrays.asList(effective_java, mama_mia);
    }
}
