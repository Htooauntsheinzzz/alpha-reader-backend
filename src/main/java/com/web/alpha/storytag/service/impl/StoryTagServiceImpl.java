package com.web.alpha.storytag.service.impl;

import com.web.alpha.storytag.dto.StoryTagCreateRequest;
import com.web.alpha.storytag.dto.StoryTagResponse;
import com.web.alpha.storytag.dto.StoryTagUpdateRequest;
import com.web.alpha.storytag.entity.StoryTag;
import com.web.alpha.storytag.event.StoryTagEvent;
import com.web.alpha.storytag.event.StoryTagEventPublisher;
import com.web.alpha.storytag.event.StoryTagEventType;
import com.web.alpha.storytag.mapper.StoryTagMapper;
import com.web.alpha.storytag.repository.StoryTagRepository;
import com.web.alpha.storytag.service.StoryTagService;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StoryTagServiceImpl implements StoryTagService {

    private static final Logger log = LoggerFactory.getLogger(StoryTagServiceImpl.class);
    private static final int NOT_DELETED = 0;

    private final StoryTagRepository storyTagRepository;
    private final StoryTagMapper storyTagMapper;
    private final StoryTagEventPublisher eventPublisher;

    public StoryTagServiceImpl(
            StoryTagRepository storyTagRepository,
            StoryTagMapper storyTagMapper,
            StoryTagEventPublisher eventPublisher
    ) {
        this.storyTagRepository = storyTagRepository;
        this.storyTagMapper = storyTagMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "story-tag-list-cache", key = "'story-tag:all'")
    public StoryTagResponse create(StoryTagCreateRequest request) {
        Long currentUserId = getCurrentUserId();
        log.info("Creating story type actorUserId={} name={}", currentUserId, request.name());
        ensureNameIsAvailable(request.name());
        StoryTag entity = storyTagMapper.toEntity(request, currentUserId);
        StoryTag savedStoryTag = storyTagRepository.save(entity);
        publishEvent(StoryTagEventType.CREATED, savedStoryTag, currentUserId);
        log.info("Story type created storyTagId={} actorUserId={}", savedStoryTag.getId(), currentUserId);
        return storyTagMapper.toResponse(savedStoryTag);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "story-tag-cache", key = "'story-tag:' + #id"),
            @CacheEvict(cacheNames = "story-tag-list-cache", key = "'story-tag:all'")
    })
    public StoryTagResponse update(Long id, StoryTagUpdateRequest request) {
        Long currentUserId = getCurrentUserId();
        log.info("Updating story type storyTagId={} actorUserId={}", id, currentUserId);
        StoryTag entity = findActiveStoryTag(id);
        if (!entity.getName().equals(request.name())) {
            ensureNameIsAvailable(request.name());
        }
        storyTagMapper.updateEntity(entity, request, currentUserId);
        StoryTag savedStoryTag = storyTagRepository.save(entity);
        publishEvent(StoryTagEventType.UPDATED, savedStoryTag, currentUserId);
        log.info("Story type updated storyTagId={} actorUserId={}", id, currentUserId);
        return storyTagMapper.toResponse(savedStoryTag);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "story-tag-cache", key = "'story-tag:' + #id"),
            @CacheEvict(cacheNames = "story-tag-list-cache", key = "'story-tag:all'")
    })
    public void delete(Long id) {
        Long currentUserId = getCurrentUserId();
        log.info("Soft deleting story type storyTagId={} actorUserId={}", id, currentUserId);
        StoryTag entity = findActiveStoryTag(id);
        entity.setIsDeleted(1);
        entity.setCreatedBy(currentUserId);
        entity.setCreatedAt(LocalDateTime.now());
        StoryTag savedStoryTag = storyTagRepository.save(entity);
        publishEvent(StoryTagEventType.DELETED, savedStoryTag, currentUserId);
        log.info("Story type soft deleted storyTagId={} actorUserId={}", id, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "story-tag-cache", key = "'story-tag:' + #id")
    public StoryTagResponse getById(Long id) {
        return storyTagMapper.toResponse(findActiveStoryTag(id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "story-tag-list-cache", key = "'story-tag:all'")
    public List<StoryTagResponse> getAll() {
        return storyTagRepository.findAllByIsDeletedOrderByIdDesc(NOT_DELETED).stream()
                .map(storyTagMapper::toResponse)
                .toList();
    }

    private StoryTag findActiveStoryTag(Long id) {
        return storyTagRepository.findByIdAndIsDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Story type not found"));
    }

    private void ensureNameIsAvailable(String name) {
        if (storyTagRepository.existsByNameAndIsDeleted(name, NOT_DELETED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Story type name already exists");
        }
    }

    private void publishEvent(StoryTagEventType eventType, StoryTag storyTag, Long currentUserId) {
        eventPublisher.publish(new StoryTagEvent(
                eventType,
                storyTag.getId(),
                storyTag.getName(),
                currentUserId,
                LocalDateTime.now()
        ));
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
