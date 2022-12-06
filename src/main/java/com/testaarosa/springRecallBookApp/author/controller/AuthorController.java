package com.testaarosa.springRecallBookApp.author.controller;

import com.testaarosa.springRecallBookApp.author.application.UpdatedAuthorResponse;
import com.testaarosa.springRecallBookApp.author.application.port.AuthorUseCase;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.globalHeaderFactory.HeaderKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authors API", description = "API designed to manipulate the Author object")
@SecurityRequirement(name = "springrecallbook-api_documentation")
class AuthorController {
    private static final String DEFAULT_QUERY_LIMIT = "3";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";
    private final AuthorUseCase authorUseCase;

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all authors from data base",
            description = "Filtering by name or/and lastName or/and yearOfBirth. Is not case sensitive. Limit default value is 3, not required")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No authors found!"),
    })
    ResponseEntity<List<Author>> getAll(@RequestParam Optional<String> name,
                                        @RequestParam Optional<String> lastName,
                                        @RequestParam Optional<Integer> yearOfBirth,
                                        @RequestParam(value = "limit", defaultValue = DEFAULT_QUERY_LIMIT, required = false)
                                        @Min(value = 1, message = "Page size cannot be less than one")int limit,
                                        @AuthenticationPrincipal UserDetails user) {

        return prepareResponseForGetAll(authorUseCase.findAllByParams(AuthorQueryCommand.builder()
                .name(name.orElse(null))
                .lastName(lastName.orElse(null))
                .yearOfBirth(yearOfBirth.orElse(null))
                .limit(limit).build()));
    }

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get author by id from data base", description = "Search for one author by data base unique ID")
    @Parameter(name = "id", required = true, description = "Searching author ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No author found!"),
    })
    ResponseEntity<Author> findOneById(@PathVariable("id")
                                       @NotNull(message = "AuthorId filed can't be null")
                                       @Min(value = 1, message = "BookId field value must be greater than 0") Long id,
                                       @AuthenticationPrincipal UserDetails user) {
        return authorUseCase.findById(id)
                .map(author -> ResponseEntity.ok()
                        .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                        .body(author))
                .orElseGet(() -> ResponseEntity.notFound()
                        .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NOT_FOUND.name())
                        .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), "Author with ID: " + id + " not found!")
                        .build());
    }

    private ResponseEntity<List<Author>> prepareResponseForGetAll(List<Author> authorList) {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(authorList);
    }

    @Secured(value = {ROLE_ADMIN})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add new author", description = "Add new author using RestAuthorCommand. All fields are validated")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Author object created successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
    })
    ResponseEntity<?> addAuthor(@Validated({CreateAuthorCommandGroup.class})
                                @RequestBody RestAuthorCommand command,
                                @AuthenticationPrincipal UserDetails user) {
        Author author = authorUseCase.addAuthor(command.toCreateAuthorCommand());
        return ResponseEntity.created(getUri(author.getId()))
                .headers(getSuccessfulHeaders(HttpStatus.CREATED, HttpMethod.POST))
                .build();
    }

    @Secured(value = {ROLE_ADMIN})
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update author object", description = "Update existing author using ID. All fields are validated")
    @Parameter(name = "id", required = true, description = "Updating author ID")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Author object updated successful"),
            @ApiResponse(responseCode = "204", description = "Can't update, no author found"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
    })
    ResponseEntity<Object> updateAuthor(@PathVariable("id") @NotNull(message = "AuthorId filed can't be null)")
                                        @Min(value = 1, message = "AuthorId field value must be greater than 0") Long id,
                                        @Validated({UpdateAuthorCommandGroup.class})
                                        @RequestBody RestAuthorCommand command,
                                        @AuthenticationPrincipal UserDetails user) {
        UpdatedAuthorResponse updatedAuthorResponse = authorUseCase.updateAuthor(command.toUpdateAuthorCommand(id));
        if (!updatedAuthorResponse.isSuccess()) {
            return ResponseEntity.noContent()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.PATCH.name())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NO_CONTENT.name())
                    .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), updatedAuthorResponse.getErrorList().toString())
                    .build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .location(getUri(id))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(getSuccessfulHeaders(HttpStatus.ACCEPTED, HttpMethod.PATCH))
                .build();
    }

    private static URI getUri(Long id) {
        return ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/catalog")
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    @Secured(value = {ROLE_ADMIN})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove author object by ID", description = "Remove author by data base ID")
    @Parameter(name = "isForceDelete", required = true, description = "If isForceDelete param is true, author will be deleted even if he has linked books")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removed successful"),
    })
    public void deleteById(@PathVariable @NotNull(message = "AuthorId filed can't be null)")
                           @Min(value = 1, message = "AuthorId field value must be greater than 0") Long id,
                           @RequestParam(value = "isForceDelete", defaultValue = "false")
                           @NotNull(message = "UploadId field can't be empty or null") Boolean isForceDelete,
                           @AuthenticationPrincipal UserDetails user) {
        authorUseCase.removeById(id, isForceDelete);
    }
}

