package com.testaarosa.springRecallBookApp.user.application;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class RegisterUserCommand {
    private String username;
    private String password;
    private String passwordMatch;
}
