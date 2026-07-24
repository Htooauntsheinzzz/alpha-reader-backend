package com.web.alpha.appgenre.mapper;

import com.web.alpha.appgenre.dto.AppGenreCreateRequest;
import com.web.alpha.appgenre.dto.AppGenreResponse;
import com.web.alpha.appgenre.dto.AppGenreUpdateRequest;
import com.web.alpha.appgenre.entity.AppGenre;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class AppGenreMapper {

	public AppGenre toEntity(AppGenreCreateRequest request, Long currentUserId) {
		AppGenre entity = new AppGenre();
		entity.setName(request.name());
		entity.setCreatedDate(request.createdDate());
		entity.setDescription(request.description());
		entity.setIsActive(1);
		entity.setIsDeleted(0);
		entity.setCreateBy(currentUserId);
		entity.setCreateAt(LocalDateTime.now());
		return entity;
	}

	public void updateEntity(AppGenre entity, AppGenreUpdateRequest request) {
		entity.setName(request.name());
		entity.setCreatedDate(request.createdDate());
		entity.setDescription(request.description());
		entity.setIsActive(request.isActive());
	}

	public AppGenreResponse toResponse(AppGenre entity) {
		return new AppGenreResponse(
				entity.getId(),
				entity.getName(),
				entity.getCreatedDate(),
				entity.getDescription(),
				entity.getIsActive(),
				entity.getIsDeleted(),
				entity.getCreateBy(),
				entity.getCreateAt()
		);
	}
}
