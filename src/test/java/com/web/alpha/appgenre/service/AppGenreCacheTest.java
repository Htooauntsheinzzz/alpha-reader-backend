package com.web.alpha.appgenre.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.web.alpha.appgenre.dto.AppGenreCreateRequest;
import com.web.alpha.appgenre.dto.AppGenreResponse;
import com.web.alpha.appgenre.dto.AppGenreUpdateRequest;
import com.web.alpha.appgenre.entity.AppGenre;
import com.web.alpha.appgenre.mapper.AppGenreMapper;
import com.web.alpha.appgenre.repository.AppGenreRepository;
import com.web.alpha.appgenre.service.impl.AppGenreServiceImpl;
import com.web.alpha.common.outbox.service.OutboxService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppGenreCacheTest.CacheTestConfig.class)
class AppGenreCacheTest {

	@Autowired
	private AppGenreService appGenreService;

	@Autowired
	private AppGenreRepository appGenreRepository;

	@Autowired
	private CacheManager cacheManager;

	private AppGenre genre;

	@BeforeEach
	void setUp() {
		reset(appGenreRepository);
		cache("genre-cache").clear();
		cache("genre-list-cache").clear();
		genre = genre(1L, "Fantasy");
		Jwt jwt = Jwt.withTokenValue("test-token")
				.header("alg", "RS256")
				.subject("1")
				.build();
		SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void cachesGenreById() {
		when(appGenreRepository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(genre));

		AppGenreResponse first = appGenreService.getById(1L);
		AppGenreResponse second = appGenreService.getById(1L);

		assertEquals(first, second);
		verify(appGenreRepository).findByIdAndIsDeleted(1L, 0);
	}

	@Test
	void rejectsActiveNameWithDifferentLetterCase() {
		when(appGenreRepository.existsByNameIgnoreCaseAndIsDeleted("fantasy", 0)).thenReturn(true);

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> appGenreService.create(
						new AppGenreCreateRequest("fantasy", LocalDate.of(2026, 7, 24), null)
				)
		);

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
	}

	@Test
	void updateRefreshesIdCacheAndEvictsListCache() {
		when(appGenreRepository.findAllByIsDeletedOrderByIdAsc(0)).thenReturn(List.of(genre));
		when(appGenreRepository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(genre));
		when(appGenreRepository.saveAndFlush(genre)).thenReturn(genre);
		appGenreService.getAll();

		AppGenreResponse response = appGenreService.update(
				1L,
				new AppGenreUpdateRequest("Epic Fantasy", LocalDate.of(2026, 7, 23), "Updated", 1)
		);

		assertEquals(response, cache("genre-cache").get("genre:1", AppGenreResponse.class));
		assertNull(cache("genre-list-cache").get("genre:all"));
	}

	@Test
	void deleteEvictsIdAndListCaches() {
		when(appGenreRepository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(genre));
		when(appGenreRepository.findAllByIsDeletedOrderByIdAsc(0)).thenReturn(List.of(genre));
		when(appGenreRepository.saveAndFlush(genre)).thenReturn(genre);
		appGenreService.getById(1L);
		appGenreService.getAll();

		appGenreService.delete(1L);

		assertNull(cache("genre-cache").get("genre:1"));
		assertNull(cache("genre-list-cache").get("genre:all"));
	}

	private Cache cache(String name) {
		Cache cache = cacheManager.getCache(name);
		assertNotNull(cache);
		return cache;
	}

	private AppGenre genre(Long id, String name) {
		AppGenre entity = new AppGenre();
		entity.setId(id);
		entity.setName(name);
		entity.setCreatedDate(LocalDate.of(2026, 7, 22));
		entity.setDescription("Description");
		entity.setIsActive(1);
		entity.setIsDeleted(0);
		entity.setCreateBy(1L);
		entity.setCreateAt(LocalDateTime.of(2026, 7, 22, 12, 0));
		return entity;
	}

	@Configuration
	@EnableCaching
	static class CacheTestConfig {

		@Bean
		CacheManager cacheManager() {
			return new ConcurrentMapCacheManager("genre-cache", "genre-list-cache");
		}

		@Bean
		AppGenreRepository appGenreRepository() {
			return org.mockito.Mockito.mock(AppGenreRepository.class);
		}

		@Bean
		AppGenreMapper appGenreMapper() {
			return new AppGenreMapper();
		}

		@Bean
		OutboxService outboxService() {
			return org.mockito.Mockito.mock(OutboxService.class);
		}

		@Bean
		AppGenreService appGenreService(
				AppGenreRepository repository,
				AppGenreMapper mapper,
				OutboxService outboxService
		) {
			return new AppGenreServiceImpl(repository, mapper, outboxService);
		}
	}
}
