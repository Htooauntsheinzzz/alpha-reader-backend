package com.web.alpha.appgenre.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.web.alpha.appgenre.dto.deserializer.AppGenreUpdateRequestDeserializer;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@JsonDeserialize(using = AppGenreUpdateRequestDeserializer.class)
public record AppGenreUpdateRequest(
		@NotBlank
		@Size(max = 300)
		String name,

		@NotNull
		LocalDate createdDate,

		@Size(max = 500)
		String description,

		@NotNull
		@Min(0)
		@Max(1)
		Integer isActive
) implements Serializable {
}
