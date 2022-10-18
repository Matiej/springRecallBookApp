package com.testaarosa.springRecallBookApp.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    public boolean isOwnerOrAdmin(String objectOwner, UserDetails user) {
        return isAdmin(user) || isOwner(objectOwner, user);
    }

    public boolean isAdmin(UserDetails user) {
        return user.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN));
    }

    private boolean isOwner(String objectOwner, UserDetails user) {
        return StringUtils.equalsIgnoreCase(objectOwner, user.getUsername());
    }
}
