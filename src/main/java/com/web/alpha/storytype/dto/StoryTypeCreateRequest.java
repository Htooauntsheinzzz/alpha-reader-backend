package com.web.alpha.storytype.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.web.alpha.storytype.dto.deserializer.StoryTypeCreateRequestDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@JsonDeserialize(using = StoryTypeCreateRequestDeserializer.class)
public record StoryTypeCreateRequest(
		@NotBlank
		@Size(max = 100)
		String name,

		LocalDate createdDate,

		@Size(max = 500)
		String description
) implements Serializable {
}
