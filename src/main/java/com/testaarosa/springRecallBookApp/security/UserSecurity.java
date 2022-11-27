package com.testaarosa.springRecallBookApp.security;

import com.testaarosa.springRecallBookApp.user.domain.UserEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class UserSecurity {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private final AuthenticationManager authenticationManager;

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

    public AuthResponse authorize(String username, String password, HttpServletRequest request) {
        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(auth);
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

        UserEntityDetails userDetails = (UserEntityDetails) auth.getPrincipal();
        return new AuthResponse(sessionId, userDetails.getUserEntity());
    }

}
