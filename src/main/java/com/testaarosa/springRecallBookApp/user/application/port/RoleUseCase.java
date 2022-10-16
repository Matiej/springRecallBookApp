package com.testaarosa.springRecallBookApp.user.application.port;

import com.testaarosa.springRecallBookApp.user.domain.RoleEnum;

public interface RoleUseCase {

    void addRole(RoleEnum roleEnum);
}
