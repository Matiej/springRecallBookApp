package com.testaarosa.springRecallBookApp.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testaarosa.springRecallBookApp.author.domain.Author;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private Integer year;
    private BigDecimal price;
    private Long bookCoverId;
    @ManyToMany()
    @JoinTable(
            name = "author_book",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @ToString.Exclude
    @JsonIgnoreProperties(value = "linkedBooks")
    private Set<Author> linkedAuthors = new HashSet<>();
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;

    public Book(String title, Integer year, BigDecimal price) {
        this.title = title;
        this.year = year;
        this.price = price;
    }

    public Book(String title, Integer year, BigDecimal price, Set<Author> linkedAuthors) {
        this.title = title;
        this.year = year;
        this.price = price;
        this.linkedAuthors = linkedAuthors;
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

