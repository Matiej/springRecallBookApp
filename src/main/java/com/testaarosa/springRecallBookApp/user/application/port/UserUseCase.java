package com.testaarosa.springRecallBookApp.user.application.port;

import com.testaarosa.springRecallBookApp.user.application.RegisterUserRecipientCommand;
import com.testaarosa.springRecallBookApp.user.application.RegisterUserResponse;
import com.testaarosa.springRecallBookApp.user.application.RegisterUserCommand;
import com.testaarosa.springRecallBookApp.user.application.UserQueryCommand;
import com.testaarosa.springRecallBookApp.user.domain.UserEntity;

import java.util.List;

public interface UserUseCase {

    List<UserEntity> getAll(UserQueryCommand command);
    RegisterUserResponse registerUser(RegisterUserCommand command);
    RegisterUserResponse registerUser(RegisterUserRecipientCommand command);

}
