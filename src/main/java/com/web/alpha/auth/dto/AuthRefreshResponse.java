package com.web.alpha.auth.dto;

public record AuthRefreshResponse(
		String tokenType,
		String accessToken,
		long expiresIn
) {
}
