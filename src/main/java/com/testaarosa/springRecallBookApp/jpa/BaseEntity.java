package com.testaarosa.springRecallBookApp.jpa;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.UUID.randomUUID;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String UUID = randomUUID().toString();
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime lastUpdatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(getUUID(), that.getUUID()) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getLastUpdatedAt(), that.getLastUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUUID(), getCreatedAt(), getLastUpdatedAt());
    }
}
