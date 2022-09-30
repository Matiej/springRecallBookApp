package com.testaarosa.springRecallBookApp.author.controller;

import com.testaarosa.springRecallBookApp.author.application.UpdatedAuthorResponse;
import com.testaarosa.springRecallBookApp.author.application.port.AuthorUseCase;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.globalHeaderFactory.HeaderKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
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
class AuthorController {
    private static final String DEFAULT_QUERY_LIMIT = "3";
    private final AuthorUseCase authorUseCase;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all authors from data base",
            description = "Filtering by name or/and lastName or/and yearOfBirth. Is not case sensitive. Limit default 3 nor required")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No books found!"),
    })
    public ResponseEntity<List<Author>> findALl(@RequestParam Optional<String> name,
                                                @RequestParam Optional<String> lastName,
                                                @RequestParam Optional<Integer> yearOfBirth,
                                                @RequestParam(value = "limit", defaultValue = DEFAULT_QUERY_LIMIT, required = false) int limit) {

        return prepareResponseForGetAll(authorUseCase.findAllByParams(AuthorQueryCommand.builder()
                .name(name.orElse(null))
                .lastName(lastName.orElse(null))
                .yearOfBirth(yearOfBirth.orElse(null))
                .limit(limit).build()));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get author by id from data base", description = "Search for one author by data base unique ID")
    @Parameter(name = "id", required = true, description = "Searching author ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No author found!"),
    })
    public ResponseEntity<Author> findOneById(@PathVariable("id")
                                              @NotNull(message = "AuthorId filed can't be null")
                                              @Min(value = 1, message = "BookId field value must be greater than 0") Long id) {
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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add new author", description = "Add new author using RestAuthorCommand. All fields are validated")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Author object created successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
    })
    public ResponseEntity<?> addAuthor(@Validated({CreateAuthorCommandGroup.class})
                                       @RequestBody RestAuthorCommand command) {
        Author author = authorUseCase.addAuthor(command.toCreateAuthorCommand());
        return ResponseEntity.created(getUri(author.getId()))
                .headers(getSuccessfulHeaders(HttpStatus.CREATED, HttpMethod.POST))
                .build();
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update author object", description = "Update existing author using ID. All fields are validated")
    @Parameter(name = "id", required = true, description = "Updating author ID")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Author object updated successful"),
            @ApiResponse(responseCode = "204", description = "Can't update, no author found"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
    })
    public ResponseEntity<Object> updateAuthor(@PathVariable("id") @NotNull(message = "AuthorId filed can't be null)")
                                               @Min(value = 1, message = "AuthorId field value must be greater than 0") Long id,
                                               @Validated({UpdateAuthorCommandGroup.class})
                                               @RequestBody RestAuthorCommand command) {
        UpdatedAuthorResponse updatedAuthorResponse = authorUseCase.updateAuthor(command.toUpdateAuthorCommand(id));
        if (!updatedAuthorResponse.isSuccess()) {
            return ResponseEntity.noContent()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.PATCH.name())
                    .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NO_CONTENT.name())
                    .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), updatedAuthorResponse.getErrorList().toString())
                    .build();
        }
        return ResponseEntity.accepted()
                .location(getUri(id))
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
                           @NotNull(message = "UploadId field can't be empty or null") Boolean isForceDelete) {
        authorUseCase.removeById(id, isForceDelete);
    }
}

