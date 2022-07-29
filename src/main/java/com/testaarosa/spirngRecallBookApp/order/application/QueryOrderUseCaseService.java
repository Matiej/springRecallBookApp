package com.testaarosa.spirngRecallBookApp.order.application;

import com.testaarosa.spirngRecallBookApp.order.application.port.QueryOrderUseCase;
import com.testaarosa.spirngRecallBookApp.order.domain.Order;
import com.testaarosa.spirngRecallBookApp.order.domain.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
class QueryOrderUseCaseService implements QueryOrderUseCase {
    private final OrderRepository orderRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
