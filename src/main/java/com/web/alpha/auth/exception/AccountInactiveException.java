package com.web.alpha.auth.exception;

import org.springframework.http.HttpStatus;

public final class AccountInactiveException extends AuthApiException {

	public AccountInactiveException() {
		super(HttpStatus.FORBIDDEN, "AUTH_ACCOUNT_INACTIVE", "User account is not active");
	}
}
