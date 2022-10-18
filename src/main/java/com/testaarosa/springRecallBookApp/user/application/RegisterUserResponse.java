package com.testaarosa.springRecallBookApp.user.application;

import com.testaarosa.springRecallBookApp.user.domain.UserEntity;
import lombok.Value;

@Value
public class RegisterUserResponse {
    Long id;
    String username;
    boolean success;
    String errorMessage;

    public static RegisterUserResponse success(UserEntity user) {
        return new RegisterUserResponse(user.getId(), user.getUsername(), true, null);
    }

    public static RegisterUserResponse failure(String errorMessage) {
        return new RegisterUserResponse(null, null, false, errorMessage);
    }
}
