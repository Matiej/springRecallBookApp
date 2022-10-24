package com.testaarosa.springRecallBookApp.order.domain;

import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class OrderItem extends BaseEntity {
    @ManyToOne()
    @JoinColumn(name = "book_id")
    private Book book;
    private int quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrderItem orderItem = (OrderItem) o;
        return quantity == orderItem.quantity && Objects.equals(book, orderItem.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), book, quantity);
    }
}
