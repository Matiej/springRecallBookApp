package com.testaarosa.springRecallBookApp.order.price;

import com.testaarosa.springRecallBookApp.order.domain.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

public class DiscountDeliveryStrategy implements DiscountStrategy {

    @Override
    public BigDecimal calculate(Order order) {
        return null;
    }
}
