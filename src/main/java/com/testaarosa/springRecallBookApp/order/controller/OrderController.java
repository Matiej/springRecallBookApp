package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.globalHeaderFactory.HeaderKey;
import com.testaarosa.springRecallBookApp.order.application.port.OrderResponse;
import com.testaarosa.springRecallBookApp.order.application.port.OrderUseCase;
import com.testaarosa.springRecallBookApp.order.application.port.QueryOrderUseCase;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Orders controller ", description = "API designed to manipulate the order object")
class OrderController {
    private final QueryOrderUseCase queryOrder;
    private final OrderUseCase orderUseCase;

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
    @Parameter(name = "id", required = true, description = "Get recipient by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No orders found!"),
    })
    public ResponseEntity<Order> getOrderById(@PathVariable("id") @NotNull(message = "OrderId filed can't be null")
                                              @Min(value = 1, message = "OrderId field value must be greater than 0") Long id) {
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
    @Operation(summary = "Add new order", description = "Add new order using recipient ID and RestOrderItem. All fields are validated")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order object created successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
            @ApiResponse(responseCode = "404", description = "Recipient or book for the order not found!"),
    })
    public ResponseEntity<Void> addOrder(@Valid @RequestBody RestPlaceOrderCommand command) {
        OrderResponse orderResponse = orderUseCase.placeOrder(command.toPlaceOrderCommand());
        URI savedUri = getUri(orderResponse.getOrderId());
        if (!orderResponse.isSuccess()) {
            return ResponseEntity.notFound()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.POST.name())
                    .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NOT_FOUND.name())
                    .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), orderResponse.getErrorList().toString())
                    .build();
        }
        return ResponseEntity.created(savedUri)
                .headers(getSuccessfulHeaders(HttpStatus.CREATED, HttpMethod.POST))
                .build();
    }

    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Update order", description = "Add an order using order id and RestOrderItem. All fields are validated")
    @Parameter(name = "id", required = true, description = "Updating order ID")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Order updated successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed. Some fields are wrong. Response contains all details."),
    })
    public ResponseEntity<?> updateOrder(@PathVariable("id") @NotNull(message = "OrderId filed can't be null")
                                         @Min(value = 1, message = "OrderId field value must be greater than 0") Long id,
                                         @Valid @RequestBody RestUpdateOrderCommand command) {
        OrderResponse updateOrderResponse = orderUseCase.updateOrder(command.toUpdateOrderCommand(id));
        if (!updateOrderResponse.isSuccess()) {
            return ResponseEntity.notFound()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, HttpMethod.PATCH.name())
                    .header(HeaderKey.STATUS.getHeaderKeyLabel(), HttpStatus.NOT_FOUND.name())
                    .header(HeaderKey.MESSAGE.getHeaderKeyLabel(), updateOrderResponse.getErrorList().toString())
                    .build();
        }
        return ResponseEntity.created(getUri(updateOrderResponse.getOrderId()))
                .headers(getSuccessfulHeaders(HttpStatus.ACCEPTED, HttpMethod.PATCH))
                .build();
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove order", description = "Remove order by ID")
    @Parameter(name = "id", required = true, description = "Remove order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order removed successful"),
    })
    public void removeOrderById(@PathVariable("id") @NotNull(message = "OrderId filed can't be null")
                                @Min(value = 1, message = "BookId field value must be greater than 0") Long id) {
        orderUseCase.removeOrderById(id);
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
