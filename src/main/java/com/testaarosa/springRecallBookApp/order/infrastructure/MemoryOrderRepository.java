package com.testaarosa.springRecallBookApp.order.infrastructure;

import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderRepository;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
class MemoryOrderRepository implements OrderRepository {
    private final Map<Long, Order> tmpOrderStorage = new ConcurrentHashMap<>();
    private final AtomicLong ID_NEXT_VALUE = new AtomicLong(0);

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(getNextId());
        }
        order.setCreatedAt(LocalDateTime.now());
        tmpOrderStorage.put(order.getId(), order);
        return order;
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(tmpOrderStorage.values());
    }

    @Override
    public List<Order> findAllByOrderStatus(OrderStatus orderStatus) {
        return tmpOrderStorage.values()
                .stream()
                .filter(order -> order.getOrderStatus().equals(orderStatus))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Order> findOrderById(Long id) {
        return Optional.ofNullable(tmpOrderStorage.get(id));
    }

    private long getNextId() {
        return ID_NEXT_VALUE.incrementAndGet();
    }
}
