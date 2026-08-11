package com.web.alpha.common.exception;

import org.springframework.http.HttpStatus;

public final class AuthenticatedUserRequiredException extends ApiException {

    public AuthenticatedUserRequiredException() {
        super(
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATED_USER_REQUIRED",
                "Authenticated user ID is unavailable"
        );
    }
}
