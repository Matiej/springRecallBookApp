package com.testaarosa.springRecallBookApp.order.application.port;

import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface QueryOrderUseCase {

    List<Order> findAll();


    List<Order> findAllByOrderStatus(OrderStatus orderStatus);

    Optional<Order> findOrderById(Long id);

}
