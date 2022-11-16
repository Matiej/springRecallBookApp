package com.testaarosa.springRecallBookApp.order.application.price;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class OrderPrice {
    BigDecimal itemsPrice;
    BigDecimal deliveryPrice;
    BigDecimal totalDiscounts;

    public BigDecimal finalPrice() {
        return itemsPrice.add(deliveryPrice).subtract(totalDiscounts);
    }

}
