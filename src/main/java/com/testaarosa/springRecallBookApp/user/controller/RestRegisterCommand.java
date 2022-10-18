package com.testaarosa.springRecallBookApp.user.controller;

import com.testaarosa.springRecallBookApp.user.application.UserCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestRegisterCommand {
    @Email(message = "Wrong email format for 'username' filed")
    String username;
    @NotBlank(message = "Filed password cannot be empty or null")
    @Size(min = 3, max = 100)
    String password;
    @NotBlank(message = "Filed passwordMatch cannot be empty or null")
    @Size(min = 3, max = 100)
    String passwordMatch;

    public UserCommand toUserCommand() {
        return UserCommand.builder()
                .username(username)
                .password(password)
                .passwordMatch(passwordMatch)
                .build();
    }
}
