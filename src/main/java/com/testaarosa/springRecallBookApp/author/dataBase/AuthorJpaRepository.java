package com.testaarosa.springRecallBookApp.author.dataBase;

import com.testaarosa.springRecallBookApp.author.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {

    @Query(value = "SELECT * FROM Author a WHERE upper(a.name) LIKE upper(concat('%', :name, '%')) " +
            " AND upper(a.last_name) LIKE upper(concat('%', :lastName, '%')) " +
            " AND a.year_of_birth = :yearOfBirth " +
            " LIMIT :limit",
            nativeQuery = true)
    List<Author> findAllByAllParamsLimited(@Param("name") String name,
                                           @Param("lastName") String lastName,
                                           @Param("yearOfBirth") Integer yearOfBirth,
                                           @Param("limit") int limit);

    @Query(value = "SELECT * FROM Author a WHERE upper(a.name) LIKE upper(concat('%', :name, '%')) " +
            " LIMIT :limit",
            nativeQuery = true)
    List<Author> findAllByName(@Param("name") String name,
                               @Param("limit") int limit);

    @Query(value = "SELECT * FROM Author a WHERE upper(a.name) LIKE upper(concat('%', :name, '%')) " +
            " AND upper(a.last_name) LIKE upper(concat('%', :lastName, '%')) " +
            " LIMIT :limit",
            nativeQuery = true)
    List<Author> findAllByNameAndLastNameLimited(@Param("name") String name,
                                                 @Param("lastName") String lastName,
                                                 @Param("limit") int limit);

    @Query(value = "SELECT * FROM Author a WHERE upper(a.name) LIKE upper(concat('%', :name, '%')) " +
            " AND a.year_of_birth = :yearOfBirth " +
            " LIMIT :limit",
            nativeQuery = true)
    List<Author> finAllByNameAndYearOfBirthLimited(String name, Integer yearOfBirth, int limit);

    @Query(value = "SELECT * FROM Author a WHERE upper(a.last_name) LIKE upper(concat('%', :lastName, '%')) " +
            " AND a.year_of_birth = :yearOfBirth " +
            " LIMIT :limit",
            nativeQuery = true)
    List<Author> findAllByYearOfBirthAndLastNameLimited(Integer yearOfBirth, String lastName, int limit);

    @Query(value = "SELECT * from Author a WHERE upper(a.last_name) LIKE upper(concat('%', :lastName, '%')) " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Author> findAllByLastName(String lastName, int limit);

    @Query(value = "SELECT * from Author a WHERE a.year_of_birth = :yearOfBirth " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Author> findAllByYearOfBirth(Integer yearOfBirth, int limit);

    @Query(value = "SELECT * from Author LIMIT :limit",
            nativeQuery = true)
    List<Author> findAllLimited(int limit);
}
