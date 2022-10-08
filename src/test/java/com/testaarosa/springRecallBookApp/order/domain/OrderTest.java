package com.testaarosa.springRecallBookApp.order.domain;

import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.order.OrderBaseTest;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class OrderTest extends OrderBaseTest {

    @Test
    @DisplayName("Should method calculate ZERO if no order items.")
    public void calculateTotalPriceOfEmptyOrderItems() {
        //given
        Order order = Order.builder()
                .orderStatus(OrderStatus.NEW)
                .itemList(Collections.emptySet())
                .build();
        //when
        BigDecimal totalPrice = order.totalPrice();
        //then
        assertThat(totalPrice).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should method calculate correct value for given order items.")
    public void calculateTotalPrice() {
        //given
        Order order = prepareOrderForTest();
        //when
        BigDecimal totalPrice = order.totalPrice();
        //then
        assertThat(totalPrice).isEqualTo(new BigDecimal(40));
    }

    private Order prepareOrderForTest() {
        return Order.builder()
                .orderStatus(OrderStatus.NEW)
                .recipient(Recipient.builder().build())
                .itemList(prepareOrderItems())
                .build();
    }

    private Set<OrderItem> prepareOrderItems() {
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setBook(prepareBooks().get(0));
        orderItem1.setQuantity(3);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setBook(prepareBooks().get(1));
        orderItem2.setQuantity(1);

        return Set.of(orderItem1, orderItem2);
    }

}