package com.testaarosa.springRecallBookApp.user.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class LoginResponse {
    private Long userId;
    private String username;
    private Set<String> roles;
    @JsonIgnore
    private String sessionId;
}
