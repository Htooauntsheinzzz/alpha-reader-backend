package com.web.alpha.appgenre.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AppGenreResponse(
		Long id,
		String name,
		LocalDate createdDate,
		String description,
		Integer isActive,
		Integer isDeleted,
		Long createBy,
		LocalDateTime createAt
) implements Serializable {
}
