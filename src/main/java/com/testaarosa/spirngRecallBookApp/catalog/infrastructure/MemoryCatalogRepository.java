package com.testaarosa.spirngRecallBookApp.catalog.infrastructure;

import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import com.testaarosa.spirngRecallBookApp.catalog.domain.CatalogRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        if(book.getId() == null) {
            book.setId(getNextId());
        }
        tmpStorage.put(book.getId(), book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(tmpStorage.get(id));
    }

    @Override
    public void removeBookById(Long id) {
        Book removedBook = tmpStorage.remove(id);
    }

    private long getNextId() {
        return ID_NEXT_VALUE.incrementAndGet();
    }
}
