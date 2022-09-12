package com.testaarosa.springRecallBookApp.catalog.dataBase;

import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository extends JpaRepository<Book, Long> {

}
