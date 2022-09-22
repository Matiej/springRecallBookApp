package com.testaarosa.springRecallBookApp.author.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testaarosa.springRecallBookApp.catalog.domain.Book;
import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Author extends BaseEntity {
    private String name;
    private String lastName;
    private Integer yearOfBirth;
    @ManyToMany(mappedBy = "linkedAuthors", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    @JsonIgnoreProperties(value = "linkedAuthors")
    private Set<Book> linkedBooks = new HashSet<>();

    public Author(String name, String lastName, Integer yearOfBirth) {
        this.name = name;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Author author = (Author) o;
        return Objects.equals(name, author.name) && Objects.equals(lastName, author.lastName) && Objects.equals(yearOfBirth, author.yearOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, lastName, yearOfBirth);
    }

    public void addBook(Book book) {
        linkedBooks.add(book);
        book.getLinkedAuthors().add(this);
    }

    public void removeBook(Book book) {
        linkedBooks.remove(book);
        book.getLinkedAuthors().remove(this);
    }
}
