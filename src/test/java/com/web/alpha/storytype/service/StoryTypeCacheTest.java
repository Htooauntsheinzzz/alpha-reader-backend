package com.web.alpha.storytype.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.web.alpha.common.security.CurrentUserProvider;
import com.web.alpha.storytype.dto.StoryTypeUpdateRequest;
import com.web.alpha.storytype.entity.StoryType;
import com.web.alpha.storytype.event.StoryTypeEventPublisher;
import com.web.alpha.storytype.mapper.StoryTypeMapper;
import com.web.alpha.storytype.repository.StoryTypeRepository;
import com.web.alpha.storytype.service.impl.StoryTypeServiceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = StoryTypeCacheTest.CacheTestConfig.class)
class StoryTypeCacheTest {

	@Autowired
	private StoryTypeService service;

	@Autowired
	private StoryTypeRepository repository;

	@Autowired
	private CacheManager cacheManager;

	private StoryType storyType;

	@BeforeEach
	void setUp() {
		reset(repository);
		cache("story-type-cache").clear();
		cache("story-type-list-cache").clear();
		storyType = storyType();
		Jwt jwt = Jwt.withTokenValue("test-token")
				.header("alg", "RS256")
				.subject("7")
				.build();
		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void cachesIdAndListQueriesWithRequestedKeys() {
		when(repository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(storyType));
		when(repository.findAllByIsDeletedOrderByIdDesc(0)).thenReturn(List.of(storyType));

		service.getById(1L);
		service.getById(1L);
		service.getAll();
		service.getAll();

		verify(repository).findByIdAndIsDeleted(1L, 0);
		verify(repository).findAllByIsDeletedOrderByIdDesc(0);
		assertNotNull(cache("story-type-cache").get("story-type:1"));
		assertNotNull(cache("story-type-list-cache").get("story-type:all"));
	}

	@Test
	void updateEvictsIdAndListCaches() {
		when(repository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(storyType));
		when(repository.findAllByIsDeletedOrderByIdDesc(0)).thenReturn(List.of(storyType));
		when(repository.save(storyType)).thenReturn(storyType);
		service.getById(1L);
		service.getAll();

		service.update(1L, new StoryTypeUpdateRequest("Webtoon", null, "Updated", 1));

		assertNull(cache("story-type-cache").get("story-type:1"));
		assertNull(cache("story-type-list-cache").get("story-type:all"));
	}

	private Cache cache(String name) {
		Cache cache = cacheManager.getCache(name);
		assertNotNull(cache);
		return cache;
	}

	private StoryType storyType() {
		StoryType entity = new StoryType();
		entity.setId(1L);
		entity.setName("Novel");
		entity.setCreatedDate(LocalDate.of(2026, 7, 22));
		entity.setDescription("Novel story type");
		entity.setIsActive(1);
		entity.setIsDeleted(0);
		entity.setCreatedBy(1L);
		entity.setCreatedAt(LocalDateTime.of(2026, 7, 22, 10, 30));
		return entity;
	}

	@Configuration
	@EnableCaching
	static class CacheTestConfig {

		@Bean
		CacheManager cacheManager() {
			return new ConcurrentMapCacheManager("story-type-cache", "story-type-list-cache");
		}

		@Bean
		StoryTypeRepository storyTypeRepository() {
			return mock(StoryTypeRepository.class);
		}

		@Bean
		StoryTypeEventPublisher storyTypeEventPublisher() {
			return mock(StoryTypeEventPublisher.class);
		}

		@Bean
		StoryTypeService storyTypeService(
				StoryTypeRepository repository,
				StoryTypeEventPublisher eventPublisher
		) {
			return new StoryTypeServiceImpl(
					repository,
					new StoryTypeMapper(),
					eventPublisher,
					new CurrentUserProvider()
			);
		}
	}
}
