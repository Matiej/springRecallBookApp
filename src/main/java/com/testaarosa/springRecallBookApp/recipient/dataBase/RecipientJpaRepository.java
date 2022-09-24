package com.testaarosa.springRecallBookApp.recipient.dataBase;

import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipientJpaRepository extends JpaRepository<Recipient, Long> {
    Optional<Recipient> findByEmailContainingIgnoreCase(String email);

    @Query(value = "SELECT * FROM Recipient r WHERE upper(r.name) LIKE upper(concat('%', :name, '%')) " +
            " AND upper(r.last_name) LIKE upper(concat('%', :lastName, '%')) " +
            " AND r.zip_code LIKE upper(concat('%', :zipCode, '%')) " +
            " LIMIT :limit",
            nativeQuery = true)
    List<Recipient> findAllByNameAndLastNameAndZipCode(@Param("name") String name,
                                                       @Param("lastName") String lastName,
                                                       @Param("zipCode") String zipCode,
                                                       @Param("limit") int limit);

    @Query(value = "SELECT * FROM Recipient r WHERE upper(r.name) LIKE upper(concat('%', :name, '%')) " +
            " AND upper(r.last_name) LIKE upper(concat('%', :lastName, '%')) " +
            " LIMIT :limit",
            nativeQuery = true)
    List<Recipient> findaAllByNameAndLastName(@Param("name") String name,
                                              @Param("lastName") String lastName,
                                              @Param("limit") int limit);

    @Query(value = "SELECT * FROM Recipient r WHERE upper(r.name) LIKE upper(concat('%', :name, '%')) " +
            " AND upper(r.zip_code) LIKE upper(concat('%', :zipCode, '%')) " +
            " LIMIT :limit",
            nativeQuery = true)
    List<Recipient> findAllByNameAndZipCode(@Param("name") String name,
                                            @Param("zipCode") String zipCode,
                                            @Param("limit") int limit);

    @Query(value = "SELECT * FROM Recipient r WHERE upper(r.last_name) LIKE upper(concat('%', :lastName, '%')) " +
            " AND upper(r.zip_code) LIKE upper(concat('%', :zipCode, '%')) " +
            " LIMIT :limit",
            nativeQuery = true)
    List<Recipient> findAllByLastNameAndZipCode(@Param("lastName") String lastName,
                                                @Param("zipCode") String zipCode,
                                                @Param("limit") int limit);

    List<Recipient> findAllByNameContainingIgnoreCase(String name, Pageable ofSize);


    List<Recipient> findAllByLastNameContainingIgnoreCase(String lastName, Pageable ofSize);

    List<Recipient> findAllByRecipientAddress_ZipCodeContainingIgnoreCase(String zipCode, Pageable pageable);

}
