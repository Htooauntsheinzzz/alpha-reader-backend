package com.web.alpha.appgenre.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.web.alpha.appgenre.dto.deserializer.AppGenreCreateRequestDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@JsonDeserialize(using = AppGenreCreateRequestDeserializer.class)
public record AppGenreCreateRequest(
		@NotBlank
		@Size(max = 300)
		String name,

		@NotNull
		LocalDate createdDate,

		@Size(max = 500)
		String description
) implements Serializable {
}
