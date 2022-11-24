package com.testaarosa.springRecallBookApp.user.controller;

import com.testaarosa.springRecallBookApp.user.application.RegisterUserCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestRegisterUser {
    @Email(message = "Wrong email format for 'username' filed")
    private String username;
    @NotBlank(message = "Filed password cannot be empty or null")
    @Size(min = 3, max = 100)
    private String password;
    @NotBlank(message = "Filed passwordMatch cannot be empty or null")
    @Size(min = 3, max = 100)
    private String passwordMatch;

    public RegisterUserCommand toUserCommand() {
        return RegisterUserCommand.builder()
                .username(username)
                .password(password)
                .passwordMatch(passwordMatch)
                .build();
    }
}
