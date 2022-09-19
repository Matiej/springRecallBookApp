package com.testaarosa.springRecallBookApp.catalog.dataBase;

import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

//    List<Book> findAllByAuthors_firstNameContainsIgnoreCase(String firstName);

}
