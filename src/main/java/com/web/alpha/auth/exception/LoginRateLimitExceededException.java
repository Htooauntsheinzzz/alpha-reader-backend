package com.web.alpha.auth.exception;

import org.springframework.http.HttpStatus;

public final class LoginRateLimitExceededException extends AuthApiException {

	public LoginRateLimitExceededException() {
		super(
				HttpStatus.TOO_MANY_REQUESTS,
				"AUTH_RATE_LIMIT_EXCEEDED",
				"Too many failed login attempts. Try again later."
		);
	}
}
