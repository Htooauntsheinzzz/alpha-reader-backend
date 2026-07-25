package com.web.alpha.storytype.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class StoryTypeJsonDeserializerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void deserializesCreateRequestWithNullableDate() throws JsonProcessingException {
		StoryTypeCreateRequest request = objectMapper.readValue(
				"{\"name\":\"Novel\",\"createdDate\":null,\"description\":\"Novel story type\"}",
				StoryTypeCreateRequest.class
		);

		assertEquals("Novel", request.name());
		assertNull(request.createdDate());
		assertTrue(validator.validate(request).isEmpty());
	}

	@Test
	void deserializesValidUpdateRequest() throws JsonProcessingException {
		StoryTypeUpdateRequest request = objectMapper.readValue(
				"{\"name\":\"Webtoon\",\"createdDate\":\"2026-07-22\",\"description\":\"Updated\",\"isActive\":1}",
				StoryTypeUpdateRequest.class
		);

		assertEquals(LocalDate.of(2026, 7, 22), request.createdDate());
		assertEquals(1, request.isActive());
		assertTrue(validator.validate(request).isEmpty());
	}

	@Test
	void rejectsInvalidDate() {
		String json = "{\"name\":\"Novel\",\"createdDate\":\"22-07-2026\"}";

		assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(json, StoryTypeCreateRequest.class));
	}

	@Test
	void rejectsUnknownProtectedField() {
		String json = "{\"name\":\"Novel\",\"createdBy\":1}";

		assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(json, StoryTypeCreateRequest.class));
	}
}
