package com.testaarosa.spirngRecallBookApp.catalog.controller;

import com.testaarosa.spirngRecallBookApp.catalog.application.port.CatalogUseCase;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.CreateBookCommandGroup;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.UpdateBookCommandGroup;
import com.testaarosa.spirngRecallBookApp.catalog.application.port.UpdateBookResponse;
import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.testaarosa.spirngRecallBookApp.catalog.controller.HttpHeaderFactory.getSuccessfulHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogUseCase catalogUseCase;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(
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

    private ResponseEntity<?> prepareResponseForGetAll(List<?> collection) {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(collection);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        return catalogUseCase.findById(id)
                .map(book -> ResponseEntity.ok()
                        .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                        .body(book))
                .orElse(ResponseEntity.notFound()
                        .headers(getSuccessfulHeaders(HttpStatus.NOT_FOUND, HttpMethod.GET))
                        .build());
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addBook(@Validated({CreateBookCommandGroup.class}) @RequestBody RestBookCommand command) {
        Book createdBook = catalogUseCase.addBook(command.toCreateBookCommand());
        URI savedUri = getUri(createdBook.getId());
        return ResponseEntity.created(savedUri)
                .headers(getSuccessfulHeaders(HttpStatus.CREATED, HttpMethod.POST))
                .build();
    }

    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateBook(@PathVariable Long id,
                                        @Validated({UpdateBookCommandGroup.class}) @RequestBody RestBookCommand command) {
        UpdateBookResponse updateBookResponse = catalogUseCase.updateBook(command.toUpdateBookCommand(id));
        if (!updateBookResponse.isSuccess()) {
            return ResponseEntity.noContent()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.PUT.name())
                    .header("Status", HttpStatus.NO_CONTENT.name())
                    .header("Message", updateBookResponse.getErrorList().toString())
                    .build();
        }
        return ResponseEntity.accepted()
                .location(getUri(id))
                .headers(getSuccessfulHeaders(HttpStatus.ACCEPTED, HttpMethod.PUT))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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
