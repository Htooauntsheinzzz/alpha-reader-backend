package com.web.alpha.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.web.alpha.common.exception.AuthenticatedUserRequiredException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class CurrentUserProviderTest {

    private final CurrentUserProvider currentUserProvider = new CurrentUserProvider();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsUserIdFromJwtSubject() {
        setJwtSubject("42");

        assertEquals(42L, currentUserProvider.getUserId());
    }

    @Test
    void rejectsMissingAuthentication() {
        assertThrows(AuthenticatedUserRequiredException.class, currentUserProvider::getUserId);
    }

    @Test
    void rejectsNonNumericJwtSubject() {
        setJwtSubject("not-a-user-id");

        assertThrows(AuthenticatedUserRequiredException.class, currentUserProvider::getUserId);
    }

    private void setJwtSubject(String subject) {
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .subject(subject)
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }
}
