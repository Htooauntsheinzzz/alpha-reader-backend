package com.web.alpha.auth.exception;

import org.springframework.http.HttpStatus;

public final class RoleNotAssignedException extends AuthApiException {

	public RoleNotAssignedException() {
		super(HttpStatus.FORBIDDEN, "AUTH_ROLE_NOT_ASSIGNED", "User role is not assigned");
	}
}
