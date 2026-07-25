package com.web.alpha.storytype.mapper;

import com.web.alpha.storytype.dto.StoryTypeCreateRequest;
import com.web.alpha.storytype.dto.StoryTypeResponse;
import com.web.alpha.storytype.dto.StoryTypeUpdateRequest;
import com.web.alpha.storytype.entity.StoryType;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class StoryTypeMapper {

	public StoryType toEntity(StoryTypeCreateRequest request, Long currentUserId) {
		StoryType entity = new StoryType();
		entity.setName(request.name());
		entity.setCreatedDate(request.createdDate());
		entity.setDescription(request.description());
		entity.setIsActive(1);
		entity.setIsDeleted(0);
		entity.setCreatedBy(currentUserId);
		entity.setCreatedAt(LocalDateTime.now());
		return entity;
	}

	public void updateEntity(StoryType entity, StoryTypeUpdateRequest request, Long currentUserId) {
		entity.setName(request.name());
		entity.setCreatedDate(request.createdDate());
		entity.setDescription(request.description());
		entity.setIsActive(request.isActive());
		entity.setCreatedBy(currentUserId);
		entity.setCreatedAt(LocalDateTime.now());
	}

	public StoryTypeResponse toResponse(StoryType entity) {
		return new StoryTypeResponse(
				entity.getId(),
				entity.getName(),
				entity.getCreatedDate(),
				entity.getDescription(),
				entity.getIsActive(),
				entity.getIsDeleted(),
				entity.getCreatedBy(),
				entity.getCreatedAt()
		);
	}
}
