package com.testaarosa.springRecallBookApp.order.price;

import com.testaarosa.springRecallBookApp.order.domain.Order;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculate(Order order);
}
