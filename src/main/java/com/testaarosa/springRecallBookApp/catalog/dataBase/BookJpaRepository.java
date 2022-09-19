package com.testaarosa.springRecallBookApp.catalog.dataBase;

import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByLinkedAuthors_nameContainsIgnoreCaseOrLinkedAuthors_lastNameContainsIgnoreCase(String name, String lastName);

    @Query("SELECT b FROM Book b JOIN b.linkedAuthors a " +
            " WHERE upper(b.title) LIKE upper(concat('%', :title, '%')) " +
            " AND (upper(a.name) LIKE upper(concat('%', :author, '%')) " +
            " OR upper(a.lastName) LIKE upper(concat('%', :author, '%'))) ")
    List<Book> findAllByTitleAndAuthorName(@Param("title") String title,
                                           @Param("author") String author);

    List<Book> findAllByTitleContainsIgnoreCase(String title);
}
