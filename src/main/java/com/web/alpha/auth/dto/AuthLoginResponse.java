package com.web.alpha.auth.dto;

public record AuthLoginResponse(
		String tokenType,
		String accessToken,
		String refreshToken,
		long expiresIn,
		long refreshExpiresIn,
		AuthenticatedUserResponse user
) {
}
