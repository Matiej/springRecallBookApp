package com.testaarosa.springRecallBookApp.user.application;

import com.testaarosa.springRecallBookApp.recipient.application.port.RecipientUseCase;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import com.testaarosa.springRecallBookApp.recipient.domain.RecipientAddress;
import com.testaarosa.springRecallBookApp.user.application.port.UserUseCase;
import com.testaarosa.springRecallBookApp.user.dataBase.RoleJpaRepository;
import com.testaarosa.springRecallBookApp.user.dataBase.UserEntityJpaRepository;
import com.testaarosa.springRecallBookApp.user.domain.Role;
import com.testaarosa.springRecallBookApp.user.domain.RoleEnum;
import com.testaarosa.springRecallBookApp.user.domain.UserEntity;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
class UserService implements UserUseCase {
    private final UserEntityJpaRepository userEntityJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PasswordEncoder encoder;
    private final RecipientUseCase recipientUseCase;

    @Override
    public List<UserEntity> getAll(UserQueryCommand command) {
        int limit = command.limit();
        String username = command.username();
        RoleEnum roleEnum = command.role();
        Optional<Role> optionalRole = getRole(roleEnum);
        if (optionalRole.isPresent() && StringUtils.isNotBlank(username)) {
            return userEntityJpaRepository.findAllByUsernameContainingIgnoreCaseAndRolesContainingIgnoreCase(username,
                    optionalRole.get(), Pageable.ofSize(limit));
        } else if (StringUtils.isNotBlank(username)) {
            return userEntityJpaRepository.findAllByUsernameContainingIgnoreCase(username, Pageable.ofSize(limit));
        } else if (optionalRole.isPresent()) {
            return userEntityJpaRepository.findAllByRolesContainingIgnoreCase(optionalRole.get(), Pageable.ofSize(limit));
        }

        return userEntityJpaRepository.findAll(Pageable.ofSize(limit)).stream().collect(Collectors.toList());
    }

    private Optional<Role> getRole(RoleEnum roleEnum) {
        if (null == roleEnum) {
            return Optional.empty();
        }
        return roleJpaRepository.findRoleByRoleEqualsIgnoreCase(roleEnum.getRole());
    }

    @Override
    @Transactional
    public RegisterUserResponse registerUser(RegisterUserCommand command) {
        if (checkUser(command.getUsername())) {
            return RegisterUserResponse.failure("User '" + command.getUsername() + "' already exist.");
        }
        String userRole = RoleEnum.USER.getRole();
        Optional<Role> optionalRole = roleJpaRepository.findRoleByRoleEqualsIgnoreCase(userRole);
        return optionalRole.map(role -> {
            UserEntity user = new UserEntity(
                    command.getUsername(),
                    encoder.encode(command.getPassword()),
                    encoder.encode(command.getPasswordMatch()));
            user.addRole(role);
            UserEntity savedUser = userEntityJpaRepository.save(user);
            return RegisterUserResponse.success(savedUser);
        }).orElseThrow(() -> new IllegalStateException("Role " + userRole + "does not exist!"));
    }

    @Override
    public RegisterUserResponse registerUser(RegisterUserRecipientCommand command) {
        if (checkUser(command.getUsername())) {
            return RegisterUserResponse.failure("User '" + command.getUsername() + "' already exist.");
        }
        if (checkRecipient(command.getEmail())) {
            return RegisterUserResponse.failure("Recipient '" + command.getEmail() + "' already exist.");
        }
        UserEntity user = register(command);
        Recipient recipient = prepareRecipient(command);
        Set<Recipient> recipients = new HashSet<>();
        recipients.add(recipient);
        recipient.setUser(user);
//        user.addRecipient(recipient);
        user.setRecipients(recipients);
        UserEntity savedUser = userEntityJpaRepository.save(user);
        return RegisterUserResponse.success(savedUser);
    }

    private UserEntity register(RegisterUserCommand command) {
        String userRole = RoleEnum.USER.getRole();
        Optional<Role> optionalRole = roleJpaRepository.findRoleByRoleEqualsIgnoreCase(userRole);
        return optionalRole.map(role -> {
            UserEntity user = new UserEntity(
                    command.getUsername(),
                    encoder.encode(command.getPassword()),
                    encoder.encode(command.getPasswordMatch()));
            user.addRole(role);
            return user;
        }).orElseThrow(() -> new IllegalStateException("Role " + userRole + "does not exist!"));
    }

    private Recipient prepareRecipient(RegisterUserRecipientCommand command) {
        return new Recipient(
                command.getName(),
                command.getLastName(),
                command.getPhone(),
                command.getEmail(),
                new RecipientAddress(command.getStreet(),
                        command.getBuildingNumber(),
                        command.getApartmentNumber(),
                        command.getDistrict(),
                        command.getCity(),
                        command.getZipCode())
        );
    }

    private boolean checkRecipient(String recipientEmail) {
        return recipientUseCase.findOneByEmail(recipientEmail).isPresent();
    }

    private boolean checkUser(String user) {
        return userEntityJpaRepository.findByUsernameIgnoreCase(user).isPresent();
    }
}
