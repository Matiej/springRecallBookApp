package com.testaarosa.spirngRecallBookApp;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
class CatalogService {
    private final Map<Long, Book> tmpStorage = new ConcurrentHashMap<>();

    public CatalogService() {
        tmpStorage.put(1L, new Book(1L, "Pan Tadeusz", "Adam Mickiewicz", 1834));
        tmpStorage.put(2L, new Book(2L, "Dziady", "Adam Mickiewicz", 1835));
        tmpStorage.put(3L, new Book(3L, "Potop", "Heryk Sienkiewicz", 1834));
        tmpStorage.put(3L, new Book(3L, "Chłopi", "Władysław Reymont", 1834));
    }

    List<Book> findByTitle(String title) {
        return tmpStorage.values()
                .stream()
                .filter(book -> book.getTitle().startsWith(title))
                .collect(Collectors.toList());
    }


}
