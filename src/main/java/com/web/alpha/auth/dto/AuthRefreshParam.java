package com.web.alpha.auth.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.web.alpha.auth.dto.deserializer.RefreshJsonDeserializer;
import jakarta.validation.constraints.NotBlank;

@JsonDeserialize(using = RefreshJsonDeserializer.class)
public record AuthRefreshParam(
		@NotBlank(message = "Refresh token is required")
		String refreshToken
) {
}
