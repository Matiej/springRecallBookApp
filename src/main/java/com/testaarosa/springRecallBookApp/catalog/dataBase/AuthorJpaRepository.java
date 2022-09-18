package com.testaarosa.springRecallBookApp.catalog.dataBase;

import com.testaarosa.springRecallBookApp.catalog.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {

    @Query(value = "SELECT * from Author a WHERE a.name = :name and a.last_name = :lastName and a.year_of_birth = :yearOfBirth LIMIT :limit",
            nativeQuery = true)
    List<Author> findAllByAllParams(@Param("name") String name,
                                    @Param("lastName") String lastName,
                                    @Param("yearOfBirth") Integer yearOfBirth,
                                    @Param("limit") int limit);
}
