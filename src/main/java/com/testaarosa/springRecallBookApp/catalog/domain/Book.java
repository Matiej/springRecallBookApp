package com.testaarosa.springRecallBookApp.catalog.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "title")
    private String title;
    private String author;
    private Integer year;
    private BigDecimal price;
    private Long bookCoverId;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;

    public Book(String title, String author, Integer year, BigDecimal price) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(year, book.year) && Objects.equals(price, book.price) && Objects.equals(bookCoverId, book.bookCoverId) && Objects.equals(createdAt, book.createdAt) && Objects.equals(lastUpdatedAt, book.lastUpdatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, year, price, bookCoverId, createdAt, lastUpdatedAt);
    }
}

