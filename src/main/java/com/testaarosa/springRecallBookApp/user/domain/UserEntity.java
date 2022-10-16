package com.testaarosa.springRecallBookApp.user.domain;

import com.testaarosa.springRecallBookApp.jpa.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
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
    private Set<Role> roles;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;
//        UserEntity userEntity = (UserEntity) o;
//        return accountNonExpired == userEntity.accountNonExpired && accountNonLocked == userEntity.accountNonLocked && credentialsNonExpired ==
//                userEntity.credentialsNonExpired && enabled == userEntity.enabled && Objects.equals(username, userEntity.username) && Objects.equals(password, userEntity.password)
//                && Objects.equals(matchingPassword, userEntity.matchingPassword);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(super.hashCode(), username, password, matchingPassword, accountNonExpired, accountNonLocked, credentialsNonExpired, enabled);
//    }
}
