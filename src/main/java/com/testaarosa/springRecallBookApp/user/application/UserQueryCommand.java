package com.testaarosa.springRecallBookApp.user.application;

import com.testaarosa.springRecallBookApp.user.domain.RoleEnum;
import lombok.Builder;

@Builder
public record UserQueryCommand(String username, RoleEnum role, int limit) {
}
