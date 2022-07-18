package com.testaarosa.spirngRecallBookApp.catalog.infrastructure;

import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import com.testaarosa.spirngRecallBookApp.catalog.domain.CatalogRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class MemoryBestsellerCatalogRepository implements CatalogRepository {
    private final Map<Long, Book> tmpStorage = new ConcurrentHashMap<>();

    public MemoryBestsellerCatalogRepository() {
        tmpStorage.put(1L, new Book(1L, "Harry Potter", "Joanna Herwing ", 2022));
        tmpStorage.put(2L, new Book(2L, "Black Out", "Wienia Karkowska", 2010));
        tmpStorage.put(3L, new Book(3L, "Sezon Burz", "Stefan Burczymucha", 2005));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(tmpStorage.values());
    }
}
