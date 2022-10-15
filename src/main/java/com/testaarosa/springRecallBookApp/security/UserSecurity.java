package com.testaarosa.springRecallBookApp.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    public boolean isOwnerOrAdmin(String objectOwner, User user) {
        return isAdmin(objectOwner, user) || isOwner(objectOwner, user);
    }

    private boolean isAdmin(String objectOwner, User user) {
        return user.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN));
    }

    private boolean isOwner(String objectOwner, User user) {
        return StringUtils.equalsIgnoreCase(objectOwner, user.getUsername());
    }
}
