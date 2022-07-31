package com.testaarosa.spirngRecallBookApp.order.application;

import com.testaarosa.spirngRecallBookApp.order.application.port.QueryOrderUseCase;
import com.testaarosa.spirngRecallBookApp.order.domain.Order;
import com.testaarosa.spirngRecallBookApp.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryOrderService implements QueryOrderUseCase {
    private final OrderRepository repository;

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }
}
