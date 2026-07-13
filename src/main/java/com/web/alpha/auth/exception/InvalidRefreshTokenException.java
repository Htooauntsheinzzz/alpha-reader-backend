package com.web.alpha.auth.exception;

import org.springframework.http.HttpStatus;

public final class InvalidRefreshTokenException extends AuthApiException {

	public InvalidRefreshTokenException() {
		super(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_REFRESH_TOKEN", "Invalid refresh token");
	}
}
