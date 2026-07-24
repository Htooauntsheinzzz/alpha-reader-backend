package com.web.alpha.auth.dto;

public record AuthenticatedUserResponse(
		Long id,
		String name,
		String email,
		String status,
		String role
) {
}
