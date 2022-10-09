package com.testaarosa.springRecallBookApp.order.price;

import com.testaarosa.springRecallBookApp.order.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class PriceService {
    private final List<DiscountStrategy> discountStrategyList = List.of(
            new DiscountTotalPriceStrategy(),
            new DiscountDeliveryStrategy()
    );

    @Transactional
    public OrderPrice calculateOrderPrice(Order order) {
        return new OrderPrice(
                order.getItemsPrice(),
                order.getDeliverPrice(),
                discounts(order));
    }

    private BigDecimal discounts(Order order) {
        return discountStrategyList.stream()
                .map(strategy -> strategy.calculate(order))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
