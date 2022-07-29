package com.testaarosa.spirngRecallBookApp.order.domain;

import com.testaarosa.spirngRecallBookApp.catalog.domain.Book;
import lombok.Value;

@Value
public class OrderItem {
    Book book;
    int quantity;
}
