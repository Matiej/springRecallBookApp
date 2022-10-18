package com.testaarosa.springRecallBookApp.user.application;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserCommand {
    String username;
    String password;
    String passwordMatch;
}
