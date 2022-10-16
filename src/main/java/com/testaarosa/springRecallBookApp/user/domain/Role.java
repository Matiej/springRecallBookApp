package com.testaarosa.springRecallBookApp.user.domain;

import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@ToString
@Entity
@NoArgsConstructor
public class Role extends BaseEntity {
    @Column(unique = true)
    private String role;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "roles", targetEntity = UserEntity.class)
    private Set<UserEntity> userEntities = new HashSet<>();

    public Role(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Role role1 = (Role) o;
        return Objects.equals(role, role1.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role);
    }
}
