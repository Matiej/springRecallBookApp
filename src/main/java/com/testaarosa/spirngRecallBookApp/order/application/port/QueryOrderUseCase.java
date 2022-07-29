package com.testaarosa.spirngRecallBookApp.order.application.port;

import com.testaarosa.spirngRecallBookApp.order.domain.Order;

import java.util.List;

public interface QueryOrderUseCase {

    List<Order> findAll();

}
