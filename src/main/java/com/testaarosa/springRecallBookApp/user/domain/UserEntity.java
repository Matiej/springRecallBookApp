package com.testaarosa.springRecallBookApp.user.domain;

import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class UserEntity extends BaseEntity {
    private String username;
    private String password;
    private String matchingPassword;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "role_user",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role> roles = new HashSet<>();

    public UserEntity(String username, String password, String matchingPassword) {
        this.username = username;
        this.password = password;
        this.matchingPassword = matchingPassword;
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(matchingPassword, that.matchingPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, password, matchingPassword);
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }
}
