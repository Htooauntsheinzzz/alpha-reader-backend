package com.web.alpha.appgenre.service.impl;

import com.web.alpha.appgenre.dto.AppGenreCreateRequest;
import com.web.alpha.appgenre.dto.AppGenreResponse;
import com.web.alpha.appgenre.dto.AppGenreUpdateRequest;
import com.web.alpha.appgenre.entity.AppGenre;
import com.web.alpha.appgenre.mapper.AppGenreMapper;
import com.web.alpha.appgenre.repository.AppGenreRepository;
import com.web.alpha.appgenre.service.AppGenreService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppGenreServiceImpl implements AppGenreService {

	private static final int NOT_DELETED = 0;

	private final AppGenreRepository appGenreRepository;
	private final AppGenreMapper appGenreMapper;

	public AppGenreServiceImpl(AppGenreRepository appGenreRepository, AppGenreMapper appGenreMapper) {
		this.appGenreRepository = appGenreRepository;
		this.appGenreMapper = appGenreMapper;
	}

	@Override
	@Transactional
	public AppGenreResponse create(AppGenreCreateRequest request) {
		ensureNameIsAvailable(request.name());
		AppGenre entity = appGenreMapper.toEntity(request, getCurrentUserId());
		return appGenreMapper.toResponse(appGenreRepository.save(entity));
	}

	@Override
	@Transactional
	public AppGenreResponse update(Long id, AppGenreUpdateRequest request) {
		AppGenre entity = findActiveGenre(id);
		if (!entity.getName().equals(request.name())) {
			ensureNameIsAvailable(request.name());
		}
		appGenreMapper.updateEntity(entity, request);
		return appGenreMapper.toResponse(appGenreRepository.save(entity));
	}

	@Override
	@Transactional
	public void delete(Long id) {
		AppGenre entity = findActiveGenre(id);
		entity.setIsDeleted(1);
		appGenreRepository.save(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public AppGenreResponse getById(Long id) {
		return appGenreMapper.toResponse(findActiveGenre(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<AppGenreResponse> getAll() {
		return appGenreRepository.findAllByIsDeletedOrderByIdAsc(NOT_DELETED).stream()
				.map(appGenreMapper::toResponse)
				.toList();
	}

	private AppGenre findActiveGenre(Long id) {
		return appGenreRepository.findByIdAndIsDeleted(id, NOT_DELETED)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "App genre not found"));
	}

	private void ensureNameIsAvailable(String name) {
		if (appGenreRepository.existsByNameAndIsDeleted(name, NOT_DELETED)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "App genre name already exists");
		}
	}

	private Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
		}

		try {
			return Long.valueOf(jwt.getSubject());
		} catch (NumberFormatException | NullPointerException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT subject must be a user id");
		}
	}
}
