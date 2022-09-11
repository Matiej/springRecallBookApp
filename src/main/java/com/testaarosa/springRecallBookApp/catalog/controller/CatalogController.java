package com.testaarosa.springRecallBookApp.catalog.controller;

import com.testaarosa.springRecallBookApp.catalog.application.port.*;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.globalHeaderFactory.HeaderKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
@Validated
@Tag(name = "CatalogController", description = "API designed to manipulate the object book ")
public class CatalogController {
    private final CatalogUseCase catalogUseCase;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all books from data base",
            description = "Filtering by title or/and author. Is not case sensitive. Limit default 3 nor required")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No books found!"),
    })
    public ResponseEntity<List<Book>> getAll(
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> author,
            @RequestParam(value = "limit", defaultValue = "3", required = false) int limit) {
        if (title.isPresent() && author.isPresent()) {
            return prepareResponseForGetAll(catalogUseCase.findByTitleAndAuthor(title.get(), author.get()));
        } else if (title.isPresent()) {
            return prepareResponseForGetAll(catalogUseCase.findByTitle(title.get()));
        } else if (author.isPresent()) {
            return prepareResponseForGetAll(catalogUseCase.findByAuthor(author.get()));
        }
        return prepareResponseForGetAll(catalogUseCase.findAll()
                .stream()
                .limit(limit)
                .collect(Collectors.toList()));
    }

    private ResponseEntity<List<Book>> prepareResponseForGetAll(List<Book> collection) {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(collection);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get book by id from data base", description = "Search for one book by data base unique ID")
    @Parameter(name = "id", required = true, description = "Searching book ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No books found!"),
    })
    public ResponseEntity<Book> getBookById(@PathVariable("id") @NotNull(message = "BookId filed can't be null")
                                            @Min(value = 1, message = "BookId field value must be more than 0") Long id) {
        return catalogUseCase.findById(id)
                .map(book -> ResponseEntity.ok()
                        .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                        .body(book))
                .orElse(ResponseEntity.notFound()
                        .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NOT_FOUND.name())
                        .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), "Book with ID: " + id + " not found!")
                        .build());
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Add new book, uses restBookCommand", description = "Add new book using restBookCommand. All fields are validated")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book object created successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
    })
    public ResponseEntity<Void> addBook(@Validated({CreateBookCommandGroup.class}) @RequestBody RestBookCommand command) {
        Book createdBook = catalogUseCase.addBook(command.toCreateBookCommand());
        URI savedUri = getUri(createdBook.getId());
        return ResponseEntity.created(savedUri)
                .headers(getSuccessfulHeaders(HttpStatus.CREATED, HttpMethod.POST))
                .build();
    }

    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Update book object", description = "Update existing book using ID. All fields are validated")
    @Parameter(name = "id", required = true, description = "Updating book ID")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Book object updated successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
    })
    public ResponseEntity<Object> updateBook(@PathVariable("id") @NotNull(message = "BookId filed can't be null")
                                             @Min(value = 1, message = "BookId field value must be more than 0") Long id,
                                             @Validated({UpdateBookCommandGroup.class}) @RequestBody RestBookCommand command) {
        UpdateBookResponse updateBookResponse = catalogUseCase.updateBook(command.toUpdateBookCommand(id));
        if (!updateBookResponse.isSuccess()) {
            return ResponseEntity.noContent()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.PATCH.name())
                    .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NO_CONTENT.name())
                    .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), updateBookResponse.getErrorList().toString())
                    .build();
        }
        return ResponseEntity.accepted()
                .location(getUri(id))
                .headers(getSuccessfulHeaders(HttpStatus.ACCEPTED, HttpMethod.PATCH))
                .build();
    }

    @PutMapping(value = "/{id}/cover", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Update book object - add cover picture", description = "Update existing book using book ID. Needed jpg picture attached")
    @Parameter(name = "id", required = true, description = "Updating book ID")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Book object updated with cover successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
    })
    public ResponseEntity<Void> addBookCover(@PathVariable("id") @NotNull(message = "BookId filed can't be null")
                                             @Min(value = 1, message = "BookId field value must be more than 0") Long id,
                                             @RequestParam(value = "cover") MultipartFile cover) throws IOException {
        log.info("Received request with file: " + cover.getOriginalFilename());
        catalogUseCase.updateBookCover(UpdateBookCoverCommand
                .builder()
                .id(id)
                .file(cover.getBytes())
                .fileName(cover.getOriginalFilename())
                .fileContentType(cover.getContentType())
                .build());
        return ResponseEntity.accepted()
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.PUT.name())
                .build();

    }

    @DeleteMapping(value = "/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove cover book picture", description = "Remove book cover picture by book ID")
    @Parameter(name = "id", required = true, description = "Removing book ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removed successful"),
    })
    public void deleteCoverByBookId(@NotNull(message = "BookId filed can't be null")
                                    @Min(value = 1, message = "BookId field value must be more than 0") Long id) {
        catalogUseCase.removeCoverByBookId(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove book object by ID", description = "Remove book by data base ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removed successful"),
    })
    public void deleteById(@PathVariable Long id) {
        catalogUseCase.removeById(id);
    }

    private static URI getUri(Long id) {
        return ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/catalog")
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
