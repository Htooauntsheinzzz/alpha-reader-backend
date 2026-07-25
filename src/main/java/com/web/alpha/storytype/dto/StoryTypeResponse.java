package com.web.alpha.storytype.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StoryTypeResponse(
		Long id,
		String name,
		LocalDate createdDate,
		String description,
		Integer isActive,
		Integer isDeleted,
		Long createdBy,
		LocalDateTime createdAt
) implements Serializable {
}
