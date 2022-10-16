package com.testaarosa.springRecallBookApp.user.application;

import com.testaarosa.springRecallBookApp.user.application.port.RoleUseCase;
import com.testaarosa.springRecallBookApp.user.dataBase.RoleJpaRepository;
import com.testaarosa.springRecallBookApp.user.domain.Role;
import com.testaarosa.springRecallBookApp.user.domain.RoleEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RoleService implements RoleUseCase {
    private final RoleJpaRepository roleJpaRepository;

    @Override
    public void addRole(RoleEnum roleEnum) {
        Role role = new Role();
        role.setRole(roleEnum.getRole());
        roleJpaRepository.save(role);
    }
}
