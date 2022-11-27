package com.testaarosa.springRecallBookApp.user.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestLogInUser {
    @Email(message = "Wrong email format for 'username' filed")
    private String username;
    @NotBlank(message = "Filed password cannot be empty or null")
    @Size(min = 3, max = 100)
    private String password;
}
