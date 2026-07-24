package com.web.alpha.appgenre.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class AppGenreJsonDeserializerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void deserializesAndValidatesCreateRequest() throws JsonProcessingException {
		AppGenreCreateRequest request = objectMapper.readValue(
				"{\"name\":\"Fantasy\",\"createdDate\":\"2026-07-22\",\"description\":\"Fantasy books\"}",
				AppGenreCreateRequest.class
		);

		assertEquals("Fantasy", request.name());
		assertEquals(LocalDate.of(2026, 7, 22), request.createdDate());
		assertEquals("Fantasy books", request.description());
		assertTrue(validator.validate(request).isEmpty());
	}

	@Test
	void deserializesAndValidatesUpdateRequest() throws JsonProcessingException {
		AppGenreUpdateRequest request = objectMapper.readValue(
				"{\"name\":\"Drama\",\"createdDate\":\"2026-07-23\",\"description\":null,\"isActive\":0}",
				AppGenreUpdateRequest.class
		);

		assertEquals("Drama", request.name());
		assertEquals(LocalDate.of(2026, 7, 23), request.createdDate());
		assertNull(request.description());
		assertEquals(0, request.isActive());
		assertTrue(validator.validate(request).isEmpty());
	}

	@Test
	void leavesMissingValuesForBeanValidation() throws JsonProcessingException {
		AppGenreCreateRequest request = objectMapper.readValue("{}", AppGenreCreateRequest.class);

		assertFalse(validator.validate(request).isEmpty());
	}

	@Test
	void rejectsInvalidDateFormat() {
		String json = "{\"name\":\"Fantasy\",\"createdDate\":\"22-07-2026\"}";

		assertThrows(
				JsonProcessingException.class,
				() -> objectMapper.readValue(json, AppGenreCreateRequest.class)
		);
	}

	@Test
	void rejectsWrongFieldTypes() {
		String json = "{\"name\":\"Drama\",\"createdDate\":\"2026-07-23\",\"isActive\":true}";

		assertThrows(
				JsonProcessingException.class,
				() -> objectMapper.readValue(json, AppGenreUpdateRequest.class)
		);
	}

	@Test
	void rejectsUnknownFields() {
		String json = "{\"name\":\"Fantasy\",\"createdDate\":\"2026-07-22\",\"isDeleted\":0}";

		assertThrows(
				JsonProcessingException.class,
				() -> objectMapper.readValue(json, AppGenreCreateRequest.class)
		);
	}
}
