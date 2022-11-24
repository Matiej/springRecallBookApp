package com.testaarosa.springRecallBookApp.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import com.testaarosa.springRecallBookApp.recipient.domain.Recipient;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity(name = "users")
@Table(name = "users")
@NoArgsConstructor
public class UserEntity extends BaseEntity {
    private String username;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String matchingPassword;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "role_user",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    @JsonIgnoreProperties("userEntities")
    private Set<Role> roles = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnoreProperties("user")
    private Set<Recipient> recipients = new HashSet<>();

    public UserEntity(String username, String password, String matchingPassword) {
        this.username = username;
        this.password = password;
        this.matchingPassword = matchingPassword;
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

    public void addRecipient(Recipient recipient) {
        this.getRecipients().add(recipient);
    }

    public void removeRecipient(Recipient recipient) {
        this.getRecipients().remove(recipient);
    }


}
