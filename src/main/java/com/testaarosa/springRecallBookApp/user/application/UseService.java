package com.testaarosa.springRecallBookApp.user.application;

import com.testaarosa.springRecallBookApp.user.application.port.UserUseCase;
import com.testaarosa.springRecallBookApp.user.dataBase.RoleJpaRepository;
import com.testaarosa.springRecallBookApp.user.dataBase.UserEntityJpaRepository;
import com.testaarosa.springRecallBookApp.user.domain.Role;
import com.testaarosa.springRecallBookApp.user.domain.RoleEnum;
import com.testaarosa.springRecallBookApp.user.domain.UserEntity;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class UseService implements UserUseCase {
    private final UserEntityJpaRepository userEntityJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    @Override
    public List<UserEntity> getAll(UserQueryCommand command) {
        String username = command.toString();
        RoleEnum roleEnum = command.role();
        int limit = command.limit();
        if (StringUtils.isNotBlank(username)) {
            return userEntityJpaRepository.findAllByUsernameContainingIgnoreCase(username, Pageable.ofSize(limit));
        }
        if (null != roleEnum) {
            Optional<Role> optionalRole = roleJpaRepository.findRoleByRoleEqualsIgnoreCase(roleEnum.getRole());
            return optionalRole.map(role -> userEntityJpaRepository.findAllByRolesContainingIgnoreCase(role, Pageable.ofSize(limit)))
                    .orElseGet(Collections::emptyList);
        }
        //todo dorobic dla 2 parametrow
        return userEntityJpaRepository.findAll(Pageable.ofSize(limit)).stream().collect(Collectors.toList());
    }
}
