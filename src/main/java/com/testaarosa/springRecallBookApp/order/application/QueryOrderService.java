package com.testaarosa.springRecallBookApp.order.application;

import com.testaarosa.springRecallBookApp.order.application.port.QueryOrderUseCase;
import com.testaarosa.springRecallBookApp.order.application.price.OrderPrice;
import com.testaarosa.springRecallBookApp.order.application.price.PriceService;
import com.testaarosa.springRecallBookApp.order.dataBase.OrderJpaRepository;
import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueryOrderService implements QueryOrderUseCase {
    private final OrderJpaRepository repository;
    private final PriceService priceService;

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Order> findAllByOrderStatus(OrderStatus orderStatus) {
        return repository.findAllByOrderStatus(orderStatus);
    }

    @Override
    @Transactional
    public Optional<RichOrder> findOrderById(Long id) {
        return Optional
                .of(repository.findById(id)
                        .map(order -> {
                            OrderPrice orderPrice = priceService.calculateOrderPrice(order);
                            return RichOrder.toRichOrderWithFinalPrice(orderPrice, order);
                        }))
                .orElseGet(Optional::empty);
    }
}
