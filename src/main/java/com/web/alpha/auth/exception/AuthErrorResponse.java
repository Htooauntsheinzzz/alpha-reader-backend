package com.web.alpha.auth.exception;

import java.util.Map;

public record AuthErrorResponse(
		String timestamp,
		int status,
		String error,
		String code,
		String message,
		String path,
		Map<String, String> fieldErrors
) {
}
