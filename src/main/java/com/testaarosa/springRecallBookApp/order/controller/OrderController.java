package com.testaarosa.springRecallBookApp.order.controller;

import com.testaarosa.springRecallBookApp.order.application.port.QueryOrderUseCase;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static com.testaarosa.springRecallBookApp.globalHeaderFactory.HttpHeaderFactory.getSuccessfulHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders controller ", description = "API designed to manipulate the order object")
public class OrderController {
    private final QueryOrderUseCase queryOrder;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "404", description = "Server has not found anything matching the requested URI! No orders found!"),
    })
    public ResponseEntity<List<Order>> getAll(@RequestParam Optional<OrderStatus> orderStatus,
                                              @RequestParam(value = "limit", defaultValue = "3", required = false) int limit) {
        if (orderStatus.isPresent()) {
            return prepareResponseForGetAll(queryOrder.findAllByOrderStatus(orderStatus.get()));
        }
        return prepareResponseForGetAll(queryOrder.findAll());
    }

    private ResponseEntity<List<Order>> prepareResponseForGetAll(List<Order> orderList) {
        return ResponseEntity.ok()
                .headers(getSuccessfulHeaders(HttpStatus.OK, HttpMethod.GET))
                .body(orderList);

    }
}
