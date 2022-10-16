package com.testaarosa.springRecallBookApp.user.dataBase;

import com.testaarosa.springRecallBookApp.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleJpaRepository extends JpaRepository<Role, Long> {
}
