package com.testaarosa.springRecallBookApp.catalog.dataBase;

import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<Book, Long>, PagingAndSortingRepository<Book, Long> {

    List<Book> findAllByLinkedAuthors_nameContainsIgnoreCaseOrLinkedAuthors_lastNameContainsIgnoreCase(String name, String lastName, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.linkedAuthors a " +
            " WHERE upper(b.title) LIKE upper(concat('%', :title, '%')) " +
            " AND (upper(a.name) LIKE upper(concat('%', :author, '%')) " +
            " OR upper(a.lastName) LIKE upper(concat('%', :author, '%'))) ")
    List<Book> findAllByTitleAndAuthorName(@Param("title") String title,
                                           @Param("author") String author,
                                           Pageable pageable);

    List<Book> findAllByTitleContainsIgnoreCase(String title, Pageable pageable);

    //join detach -> to avoid N+1 notation
    @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.linkedAuthors")
    List<Book> findAllEager(Pageable pageable);
}
