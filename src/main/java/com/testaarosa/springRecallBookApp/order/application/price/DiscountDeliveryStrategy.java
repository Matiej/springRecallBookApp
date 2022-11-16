package com.testaarosa.springRecallBookApp.order.application.price;

import com.testaarosa.springRecallBookApp.order.domain.Order;

import java.math.BigDecimal;

public class DiscountDeliveryStrategy implements DiscountStrategy {
    public static final BigDecimal THRESHOLD = BigDecimal.valueOf(100);

    @Override
    public BigDecimal calculate(Order order) {
        if(order.getItemsPrice().compareTo(THRESHOLD) >= 0) {
            return order.getDeliverPrice();
        }
        return BigDecimal.ZERO;
    }
}
