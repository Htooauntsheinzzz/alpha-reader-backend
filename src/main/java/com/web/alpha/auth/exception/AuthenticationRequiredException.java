package com.web.alpha.auth.exception;

import org.springframework.http.HttpStatus;

public final class AuthenticationRequiredException extends AuthApiException {

	public AuthenticationRequiredException() {
		super(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED", "Authentication is required");
	}
}
