package com.testaarosa.springRecallBookApp.order.application.price;

import com.testaarosa.springRecallBookApp.order.domain.Order;
import com.testaarosa.springRecallBookApp.order.domain.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public class DiscountTotalPriceStrategy implements DiscountStrategy {
    private static final int FREE_BOOK_TOTAL_VALUE_LIMIT = 400;
    private static final int HALF_PRICE_BOOK_TOTAL_VALUE_LIMIT = 200;

    @Override
    public BigDecimal calculate(Order order) {
        BigDecimal lowestBookPrice = lowestBookPrice(order.getOrderItems());
        if (isaBoolean(order, FREE_BOOK_TOTAL_VALUE_LIMIT)) {
            //cheapest book for free
            return lowestBookPrice;
        } else if (isaBoolean(order, HALF_PRICE_BOOK_TOTAL_VALUE_LIMIT)) {
            //cheapest book half price
            return lowestBookPrice.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private boolean isaBoolean(Order order, int value) {
        return order.getItemsPrice().compareTo(BigDecimal.valueOf(value)) >= 0;
    }

    private BigDecimal lowestBookPrice(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getBook().getPrice())
                .sorted()
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }
}
