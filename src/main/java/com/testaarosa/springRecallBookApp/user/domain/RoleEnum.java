package com.testaarosa.springRecallBookApp.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    ADMIN("ROLE_ADMIN"), USER("ROLE_USER");

    private final String role;
}
