package com.testaarosa.spirngRecallBookApp.catalog.infrastructure;

import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import com.testaarosa.spirngRecallBookApp.catalog.domain.CatalogRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
class MemoryCatalogRepository implements CatalogRepository {
    private final Map<Long, Book> tmpStorage = new ConcurrentHashMap<>();
    private final AtomicLong ID_NEXT_VALUE = new AtomicLong(0);

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(tmpStorage.values());
    }

    @Override
    public void save(Book book) {
        book.setId(getNextId());
        tmpStorage.put(book.getId(), book);
    }

    private long getNextId() {
        return ID_NEXT_VALUE.getAndIncrement();
    }
}
