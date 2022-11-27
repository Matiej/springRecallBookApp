package com.testaarosa.springRecallBookApp.security;

import com.testaarosa.springRecallBookApp.user.domain.UserEntity;
import lombok.Value;
import org.springframework.http.ResponseCookie;

@Value
public class AuthResponse {
    ResponseCookie cookie;
    UserEntity user;
}
