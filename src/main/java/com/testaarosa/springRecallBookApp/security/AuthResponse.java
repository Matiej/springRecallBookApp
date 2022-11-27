package com.testaarosa.springRecallBookApp.security;

import com.testaarosa.springRecallBookApp.user.domain.UserEntity;
import lombok.Value;

@Value
public class AuthResponse {
    String sessionId;
    UserEntity user;
}
