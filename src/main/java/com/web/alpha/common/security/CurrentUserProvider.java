package com.web.alpha.common.security;

import com.web.alpha.common.exception.AuthenticatedUserRequiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public final class CurrentUserProvider {

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AuthenticatedUserRequiredException();
        }
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException | NullPointerException exception) {
            throw new AuthenticatedUserRequiredException();
        }
    }
}
