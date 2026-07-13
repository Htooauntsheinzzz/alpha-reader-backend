package com.web.alpha.auth.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.web.alpha.auth.dto.deserializer.LogoutJsonDeserializer;
import jakarta.validation.constraints.NotBlank;

@JsonDeserialize(using = LogoutJsonDeserializer.class)
public record AuthLogoutParam(
		@NotBlank(message = "Refresh token is required")
		String refreshToken
) {
}
