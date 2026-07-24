package com.web.alpha.auth.service;

public record RefreshTokenData(
		Long userId,
		String email,
		String role,
		long createdAtEpochSecond
) {
}
