package com.testaarosa.spirngRecallBookApp.catalog.domain;

import java.util.List;

public interface CatalogRepository {

    List<Book> findAll();
}
