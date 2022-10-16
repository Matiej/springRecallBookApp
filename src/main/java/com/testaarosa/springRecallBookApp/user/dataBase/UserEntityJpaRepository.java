package com.testaarosa.springRecallBookApp.user.dataBase;

import com.testaarosa.springRecallBookApp.user.domain.Role;
import com.testaarosa.springRecallBookApp.user.domain.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserEntityJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    List<UserEntity> findAllByUsernameContainingIgnoreCase(String username, Pageable pageable);
    List<UserEntity> findAllByRolesContainingIgnoreCase(Role role, Pageable pageable);
}
