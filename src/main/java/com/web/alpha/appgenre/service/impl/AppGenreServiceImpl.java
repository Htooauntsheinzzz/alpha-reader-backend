package com.web.alpha.appgenre.service.impl;

import com.web.alpha.appgenre.dto.AppGenreCreateRequest;
import com.web.alpha.appgenre.dto.AppGenreResponse;
import com.web.alpha.appgenre.dto.AppGenreUpdateRequest;
import com.web.alpha.appgenre.entity.AppGenre;
import com.web.alpha.appgenre.mapper.AppGenreMapper;
import com.web.alpha.appgenre.repository.AppGenreRepository;
import com.web.alpha.appgenre.service.AppGenreService;
import com.web.alpha.common.outbox.service.OutboxService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppGenreServiceImpl implements AppGenreService {

	private static final Logger log = LoggerFactory.getLogger(AppGenreServiceImpl.class);
	private static final int NOT_DELETED = 0;
	private static final String AGGREGATE_TYPE = "APP_GENRE";

	private final AppGenreRepository appGenreRepository;
	private final AppGenreMapper appGenreMapper;
	private final OutboxService outboxService;

	public AppGenreServiceImpl(
			AppGenreRepository appGenreRepository,
			AppGenreMapper appGenreMapper,
			OutboxService outboxService
	) {
		this.appGenreRepository = appGenreRepository;
		this.appGenreMapper = appGenreMapper;
		this.outboxService = outboxService;
	}

	@Override
	@Transactional
	@Caching(
			put = @CachePut(cacheNames = "genre-cache", key = "'genre:' + #result.id()"),
			evict = @CacheEvict(cacheNames = "genre-list-cache", key = "'genre:all'")
	)
	public AppGenreResponse create(AppGenreCreateRequest request) {
		Long currentUserId = getCurrentUserId();
		log.info("Creating app genre actorUserId={} name={}", currentUserId, request.name());
		ensureNameIsAvailable(request.name());
		AppGenre entity = appGenreMapper.toEntity(request, currentUserId);
		AppGenre savedGenre = saveGenre(entity);
		recordEvent("APP_GENRE_CREATED", "app-genre.created", savedGenre, currentUserId);
		log.info("App genre created genreId={} actorUserId={}", savedGenre.getId(), currentUserId);
		return appGenreMapper.toResponse(savedGenre);
	}

	@Override
	@Transactional
	@Caching(
			put = @CachePut(cacheNames = "genre-cache", key = "'genre:' + #result.id()"),
			evict = @CacheEvict(cacheNames = "genre-list-cache", key = "'genre:all'")
	)
	public AppGenreResponse update(Long id, AppGenreUpdateRequest request) {
		Long currentUserId = getCurrentUserId();
		log.info("Updating app genre genreId={} actorUserId={}", id, currentUserId);
		AppGenre entity = findActiveGenre(id);
		if (!entity.getName().equalsIgnoreCase(request.name())) {
			ensureNameIsAvailable(request.name());
		}
		appGenreMapper.updateEntity(entity, request);
		AppGenre savedGenre = saveGenre(entity);
		recordEvent("APP_GENRE_UPDATED", "app-genre.updated", savedGenre, currentUserId);
		log.info("App genre updated genreId={} actorUserId={}", id, currentUserId);
		return appGenreMapper.toResponse(savedGenre);
	}

	@Override
	@Transactional
	@Caching(evict = {
			@CacheEvict(cacheNames = "genre-cache", key = "'genre:' + #id"),
			@CacheEvict(cacheNames = "genre-list-cache", key = "'genre:all'")
	})
	public void delete(Long id) {
		Long currentUserId = getCurrentUserId();
		log.info("Soft deleting app genre genreId={} actorUserId={}", id, currentUserId);
		AppGenre entity = findActiveGenre(id);
		entity.setIsDeleted(1);
		AppGenre savedGenre = appGenreRepository.saveAndFlush(entity);
		recordEvent("APP_GENRE_DELETED", "app-genre.deleted", savedGenre, currentUserId);
		log.info("App genre soft deleted genreId={} actorUserId={}", id, currentUserId);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "genre-cache", key = "'genre:' + #id")
	public AppGenreResponse getById(Long id) {
		return appGenreMapper.toResponse(findActiveGenre(id));
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "genre-list-cache", key = "'genre:all'")
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
		if (appGenreRepository.existsByNameIgnoreCaseAndIsDeleted(name, NOT_DELETED)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "App genre name already exists");
		}
	}

	private AppGenre saveGenre(AppGenre genre) {
		try {
			return appGenreRepository.saveAndFlush(genre);
		} catch (DataIntegrityViolationException exception) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "App genre name already exists", exception);
		}
	}

	private void recordEvent(
			String eventType,
			String routingKey,
			AppGenre genre,
			Long currentUserId
	) {
		outboxService.record(
				eventType,
				AGGREGATE_TYPE,
				genre.getId().toString(),
				routingKey,
				currentUserId,
				Map.of(
						"name", genre.getName(),
						"isActive", genre.getIsActive(),
						"isDeleted", genre.getIsDeleted()
				)
		);
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
