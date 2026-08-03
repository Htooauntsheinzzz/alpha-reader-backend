package com.web.alpha.storytag.mapper;

import com.web.alpha.storytag.dto.StoryTagCreateRequest;
import com.web.alpha.storytag.dto.StoryTagResponse;
import com.web.alpha.storytag.dto.StoryTagUpdateRequest;
import com.web.alpha.storytag.entity.StoryTag;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class StoryTagMapper {

    public StoryTag toEntity(StoryTagCreateRequest request, Long currentUserId) {
        StoryTag entity = new StoryTag();
        entity.setName(request.name());
        entity.setCreatedDate(request.createdDate());
        entity.setDescription(request.description());
        entity.setIsActive(1);
        entity.setIsDeleted(0);
        entity.setCreatedBy(currentUserId);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public void updateEntity(StoryTag entity, StoryTagUpdateRequest request, Long currentUserId) {
        entity.setName(request.name());
        entity.setCreatedDate(request.createdDate());
        entity.setDescription(request.description());
        entity.setIsActive(request.isActive());
        entity.setCreatedBy(currentUserId);
        entity.setCreatedAt(LocalDateTime.now());
    }

    public StoryTagResponse toResponse(StoryTag entity) {
        return new StoryTagResponse(
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