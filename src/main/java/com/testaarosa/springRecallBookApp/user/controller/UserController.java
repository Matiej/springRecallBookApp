package com.testaarosa.springRecallBookApp.user.controller;

import com.testaarosa.springRecallBookApp.globalHeaderFactory.HeaderKey;
import com.testaarosa.springRecallBookApp.user.application.RegisterUserResponse;
import com.testaarosa.springRecallBookApp.user.application.UserQueryCommand;
import com.testaarosa.springRecallBookApp.user.application.port.UserUseCase;
import com.testaarosa.springRecallBookApp.user.domain.RoleEnum;
import com.testaarosa.springRecallBookApp.user.domain.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users API", description = "API designed to manipulate the object user.")
@SecurityRequirement(name = "springrecallbook-api_documentation")
public class UserController {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";
    private final UserUseCase userUseCase;


    @Secured(value = ROLE_ADMIN)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all users from data base",
            description = "Filtering by name or/and role. Is not case sensitive. Limit default 3, not required")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No books found!"),
    })
    ResponseEntity<?> getAll(
            @RequestParam Optional<String> username,
            @RequestParam Optional<RoleEnum> role,
            @RequestParam(value = "limit", defaultValue = "3", required = false) int limit) {
        UserQueryCommand.UserQueryCommandBuilder builder = UserQueryCommand.builder();
        username.ifPresent(builder::username);
        role.ifPresent(builder::role);
        UserQueryCommand command = builder.limit(limit).build();
        List<UserEntity> users = userUseCase.getAll(command);
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(users);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register new user", description = "Add and register new user. All fields are validated")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User object created successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details.")
    })
    ResponseEntity<Void> register(@Valid @RequestBody RestRegisterCommand command) {
        log.info("Received request to register user: " + command.getUsername());
        RegisterUserResponse registerUserResponse = userUseCase.registerUser(command.toUserCommand());
        if (!registerUserResponse.isSuccess()) {
            return ResponseEntity.badRequest()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.POST.name())
                    .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.BAD_REQUEST.name())
                    .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), registerUserResponse.getErrorMessage())
                    .build();
        }
        return ResponseEntity.created(getUri(registerUserResponse.getId()))
                .headers(getSuccessfulHeaders(HttpStatus.CREATED, HttpMethod.POST))
                .build();
    }

    private static URI getUri(Long id) {
        return ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/users")
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
