package com.web.alpha.auth.exception;

import org.springframework.http.HttpStatus;

public final class InvalidCredentialsException extends AuthApiException {

	public InvalidCredentialsException() {
		super(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_CREDENTIALS", "Invalid email or password");
	}
}
