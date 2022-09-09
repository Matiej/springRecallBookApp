package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.globalHeaderFactory.HeaderKey;
import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderResponse;
import com.testaarosa.springRecallBookApp.order.application.port.PlaceOrderUseCase;
import com.testaarosa.springRecallBookApp.order.application.port.QueryOrderUseCase;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders controller ", description = "API designed to manipulate the order object")
class OrderController {
    private final QueryOrderUseCase queryOrder;
    private final PlaceOrderUseCase placeOrderUseCase;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all orders from data base",
            description = "Filtering by order status. It is enum. Limit default 3 nor required")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No orders found!"),
    })
    public ResponseEntity<List<Order>> getAll(@RequestParam Optional<OrderStatus> orderStatus,
                                              @RequestParam(value = "limit", defaultValue = "3", required = false) int limit) {
        return orderStatus
                .map(status -> prepareResponseForGetAll(queryOrder.findAllByOrderStatus(status)))
                .orElseGet(() -> prepareResponseForGetAll(queryOrder.findAll()
                        .stream()
                        .limit(limit)
                        .toList()));
    }

    private ResponseEntity<List<Order>> prepareResponseForGetAll(List<Order> orderList) {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(orderList);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Get order by ID from data base",
            description = "Find order by ID in data base")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No orders found!"),
    })
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return queryOrder.findOrderById(id)
                .map(order -> ResponseEntity.ok()
                        .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                        .body(order))
                .orElse(ResponseEntity.notFound()
                        .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NOT_FOUND.name())
                        .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), "Order with ID: " + id + " not found!")
                        .build());
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Add new order", description = "Add new order using receipiet id and RestOrderItem. All fields are validated")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order object created successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
            @ApiResponse(responseCode = "404", description = "Recipient or book for the order not found!"),
    })
    public ResponseEntity<Void> addOrder(@Valid @RequestBody RestPlaceOrderCommand command) {
        PlaceOrderResponse placeOrderResponse = placeOrderUseCase.placeOrder(command.toPlaceOrderCommand());
        URI savedUri = getUri(placeOrderResponse.getOrderId());
        if (!placeOrderResponse.isSuccess()) {
            return ResponseEntity.notFound()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.POST.name())
                    .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NOT_FOUND.name())
                    .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), placeOrderResponse.getErrorList().toString())
                    .build();
        }
        return ResponseEntity.created(savedUri)
                .headers(getSuccessfulHeaders(HttpStatus.CREATED, HttpMethod.POST))
                .build();
    }

    private static URI getUri(Long id) {
        return ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/orders")
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
