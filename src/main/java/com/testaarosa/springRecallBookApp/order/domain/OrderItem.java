package com.testaarosa.springRecallBookApp.order.domain;

import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import lombok.Value;

@Value
public class OrderItem {
    Book book;
    int quantity;
}
