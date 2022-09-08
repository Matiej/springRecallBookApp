package com.testaarosa.springRecallBookApp.order.application.port;

import com.testaarosa.springRecallBookApp.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {

    List<Order> findAll();

}
