package com.testaarosa.springRecallBookApp.order.price;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class OrderPrice {
    BigDecimal itemsPrice;
    BigDecimal deliveryPrice;
    BigDecimal totalDiscounts;

    public BigDecimal finalPrice() {
        return itemsPrice.add(deliveryPrice).min(totalDiscounts);
    }

}
