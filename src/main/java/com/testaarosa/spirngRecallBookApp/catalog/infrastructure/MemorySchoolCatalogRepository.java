package com.testaarosa.spirngRecallBookApp.catalog.infrastructure;

import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import com.testaarosa.spirngRecallBookApp.catalog.domain.CatalogRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
class MemorySchoolCatalogRepository implements CatalogRepository {
    private final Map<Long, Book> tmpStorage = new ConcurrentHashMap<>();

    public MemorySchoolCatalogRepository() {
        tmpStorage.put(1L, new Book(1L, "Pan Tadeusz", "Adam Mickiewicz", 1834));
        tmpStorage.put(2L, new Book(2L, "Dziady", "Adam Mickiewicz", 1835));
        tmpStorage.put(3L, new Book(3L, "Potop", "Heryk Sienkiewicz", 1834));
        tmpStorage.put(4L, new Book(4L, "Chłopi", "Władysław Reymont", 1811));
        tmpStorage.put(5L, new Book(5L, "Pan Samochodzik", "Krol Julian", 1911));
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(tmpStorage.values());
    }
}
