package com.web.alpha.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class AuthJsonDeserializerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void deserializesAndValidatesLoginRequest() throws JsonProcessingException {
		AuthLoginParam request = objectMapper.readValue(
				"{\"email\":\"alphasuperadmin@gmail.com\",\"password\":\"alpha@67passw0rd\"}",
				AuthLoginParam.class
		);

		assertEquals("alphasuperadmin@gmail.com", request.email());
		assertEquals("alpha@67passw0rd", request.password());
		assertTrue(validator.validate(request).isEmpty());
	}

	@Test
	void leavesMissingValuesForBeanValidation() throws JsonProcessingException {
		AuthLoginParam request = objectMapper.readValue("{}", AuthLoginParam.class);

		assertFalse(validator.validate(request).isEmpty());
	}

	@Test
	void rejectsNonStringValues() {
		String json = "{\"email\":123,\"password\":\"password\"}";

		assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(json, AuthLoginParam.class));
	}

	@Test
	void rejectsUnknownFields() {
		String json = "{\"email\":\"admin@example.com\",\"password\":\"password\",\"admin\":true}";

		assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(json, AuthLoginParam.class));
	}
}
