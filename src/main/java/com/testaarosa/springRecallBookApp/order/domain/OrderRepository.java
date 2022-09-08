package com.testaarosa.springRecallBookApp.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);
    List<Order> findAll();

    List<Order> findAllByOrderStatus(OrderStatus orderStatus);
}
