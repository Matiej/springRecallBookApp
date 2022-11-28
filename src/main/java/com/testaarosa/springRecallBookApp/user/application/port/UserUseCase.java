package com.testaarosa.springRecallBookApp.user.application.port;

import com.testaarosa.springRecallBookApp.user.application.*;
import com.testaarosa.springRecallBookApp.user.controller.RestLogInUser;
import com.testaarosa.springRecallBookApp.user.domain.UserEntity;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserUseCase {

    List<UserEntity> getAll(UserQueryCommand command);
    RegisterUserResponse registerUser(RegisterUserCommand command);
    RegisterUserResponse registerUser(RegisterUserRecipientCommand command);
    LoginResponse logIn(RestLogInUser logInUser, HttpServletRequest request);
    ResponseCookie logout();

}
