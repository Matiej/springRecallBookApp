package com.testaarosa.springRecallBookApp.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {
    @Column(name = "title", unique = true)
    private String title;
    private Integer year;
    private BigDecimal price;
    private Long bookCoverId;
    private Long available;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "author_book",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @ToString.Exclude
    @JsonIgnoreProperties(value = "linkedBooks")
    private Set<Author> linkedAuthors = new HashSet<>();

    public Book(String title, Integer year, BigDecimal price, Long available) {
        this.title = title;
        this.year = year;
        this.price = price;
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) && Objects.equals(year, book.year) && Objects.equals(price, book.price) && Objects.equals(bookCoverId, book.bookCoverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, year, price, bookCoverId);
    }

    public void addAuthor(Author author) {
        linkedAuthors.add(author);
        author.getLinkedBooks().add(this);
    }

    public void removeAuthor(Author author) {
        linkedAuthors.remove(author);
        author.getLinkedBooks().remove(this);
    }

    public void removeAuthors() {
        linkedAuthors.forEach(author -> author.getLinkedBooks().remove(this));
        linkedAuthors.clear();
    }
}

