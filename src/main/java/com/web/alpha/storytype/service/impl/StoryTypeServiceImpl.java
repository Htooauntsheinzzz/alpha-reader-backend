package com.web.alpha.storytype.service.impl;

import com.web.alpha.storytype.dto.StoryTypeCreateRequest;
import com.web.alpha.storytype.dto.StoryTypeResponse;
import com.web.alpha.storytype.dto.StoryTypeUpdateRequest;
import com.web.alpha.storytype.entity.StoryType;
import com.web.alpha.storytype.exception.StoryTypeNameAlreadyExistsException;
import com.web.alpha.storytype.exception.StoryTypeNotFoundException;
import com.web.alpha.storytype.event.StoryTypeEvent;
import com.web.alpha.storytype.event.StoryTypeEventPublisher;
import com.web.alpha.storytype.event.StoryTypeEventType;
import com.web.alpha.storytype.mapper.StoryTypeMapper;
import com.web.alpha.storytype.repository.StoryTypeRepository;
import com.web.alpha.storytype.service.StoryTypeService;
import com.web.alpha.common.security.CurrentUserProvider;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoryTypeServiceImpl implements StoryTypeService {

	private static final Logger log = LoggerFactory.getLogger(StoryTypeServiceImpl.class);
	private static final int NOT_DELETED = 0;

	private final StoryTypeRepository storyTypeRepository;
	private final StoryTypeMapper storyTypeMapper;
	private final StoryTypeEventPublisher eventPublisher;
	private final CurrentUserProvider currentUserProvider;

	public StoryTypeServiceImpl(
			StoryTypeRepository storyTypeRepository,
			StoryTypeMapper storyTypeMapper,
			StoryTypeEventPublisher eventPublisher,
			CurrentUserProvider currentUserProvider
	) {
		this.storyTypeRepository = storyTypeRepository;
		this.storyTypeMapper = storyTypeMapper;
		this.eventPublisher = eventPublisher;
		this.currentUserProvider = currentUserProvider;
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "story-type-list-cache", key = "'story-type:all'")
	public StoryTypeResponse create(StoryTypeCreateRequest request) {
		Long currentUserId = currentUserProvider.getUserId();
		log.info("Creating story type actorUserId={} name={}", currentUserId, request.name());
		ensureNameIsAvailable(request.name());
		StoryType entity = storyTypeMapper.toEntity(request, currentUserId);
		StoryType savedStoryType = storyTypeRepository.save(entity);
		publishEvent(StoryTypeEventType.CREATED, savedStoryType, currentUserId);
		log.info("Story type created storyTypeId={} actorUserId={}", savedStoryType.getId(), currentUserId);
		return storyTypeMapper.toResponse(savedStoryType);
	}

	@Override
	@Transactional
	@Caching(evict = {
			@CacheEvict(cacheNames = "story-type-cache", key = "'story-type:' + #id"),
			@CacheEvict(cacheNames = "story-type-list-cache", key = "'story-type:all'")
	})
	public StoryTypeResponse update(Long id, StoryTypeUpdateRequest request) {
		Long currentUserId = currentUserProvider.getUserId();
		log.info("Updating story type storyTypeId={} actorUserId={}", id, currentUserId);
		StoryType entity = findActiveStoryType(id);
		if (!entity.getName().equals(request.name())) {
			ensureNameIsAvailable(request.name());
		}
		storyTypeMapper.updateEntity(entity, request, currentUserId);
		StoryType savedStoryType = storyTypeRepository.save(entity);
		publishEvent(StoryTypeEventType.UPDATED, savedStoryType, currentUserId);
		log.info("Story type updated storyTypeId={} actorUserId={}", id, currentUserId);
		return storyTypeMapper.toResponse(savedStoryType);
	}

	@Override
	@Transactional
	@Caching(evict = {
			@CacheEvict(cacheNames = "story-type-cache", key = "'story-type:' + #id"),
			@CacheEvict(cacheNames = "story-type-list-cache", key = "'story-type:all'")
	})
	public void delete(Long id) {
		Long currentUserId = currentUserProvider.getUserId();
		log.info("Soft deleting story type storyTypeId={} actorUserId={}", id, currentUserId);
		StoryType entity = findActiveStoryType(id);
		entity.setIsDeleted(1);
		entity.setCreatedBy(currentUserId);
		entity.setCreatedAt(LocalDateTime.now());
		StoryType savedStoryType = storyTypeRepository.save(entity);
		publishEvent(StoryTypeEventType.DELETED, savedStoryType, currentUserId);
		log.info("Story type soft deleted storyTypeId={} actorUserId={}", id, currentUserId);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "story-type-cache", key = "'story-type:' + #id")
	public StoryTypeResponse getById(Long id) {
		return storyTypeMapper.toResponse(findActiveStoryType(id));
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "story-type-list-cache", key = "'story-type:all'")
	public List<StoryTypeResponse> getAll() {
		return storyTypeRepository.findAllByIsDeletedOrderByIdDesc(NOT_DELETED).stream()
				.map(storyTypeMapper::toResponse)
				.toList();
	}

	private StoryType findActiveStoryType(Long id) {
		return storyTypeRepository.findByIdAndIsDeleted(id, NOT_DELETED)
				.orElseThrow(StoryTypeNotFoundException::new);
	}

	private void ensureNameIsAvailable(String name) {
		if (storyTypeRepository.existsByNameAndIsDeleted(name, NOT_DELETED)) {
			throw new StoryTypeNameAlreadyExistsException();
		}
	}

	private void publishEvent(StoryTypeEventType eventType, StoryType storyType, Long currentUserId) {
		eventPublisher.publish(new StoryTypeEvent(
				eventType,
				storyType.getId(),
				storyType.getName(),
				currentUserId,
				LocalDateTime.now()
		));
	}

}
