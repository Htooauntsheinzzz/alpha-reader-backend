package com.web.alpha.auth.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.web.alpha.auth.dto.deserializer.LoginJsonDeserializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonDeserialize(using = LoginJsonDeserializer.class)
public record AuthLoginParam(
		@NotBlank(message = "Email is required")
		@Email(message = "Email must be valid")
		@Size(max = 150, message = "Email must not exceed 150 characters")
		String email,

		@NotBlank(message = "Password is required")
		@Size(max = 255, message = "Password must not exceed 255 characters")
		String password
) {
}
