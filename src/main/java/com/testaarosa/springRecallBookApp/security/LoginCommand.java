package com.testaarosa.springRecallBookApp.security;

import lombok.Data;

@Data
class LoginCommand {
    private String username;
    private String password;
}
