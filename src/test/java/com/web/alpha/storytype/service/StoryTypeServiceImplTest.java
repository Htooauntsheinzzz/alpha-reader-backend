package com.web.alpha.storytype.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.web.alpha.storytype.dto.StoryTypeCreateRequest;
import com.web.alpha.storytype.dto.StoryTypeResponse;
import com.web.alpha.storytype.dto.StoryTypeUpdateRequest;
import com.web.alpha.storytype.entity.StoryType;
import com.web.alpha.storytype.event.StoryTypeEvent;
import com.web.alpha.storytype.event.StoryTypeEventPublisher;
import com.web.alpha.storytype.event.StoryTypeEventType;
import com.web.alpha.storytype.mapper.StoryTypeMapper;
import com.web.alpha.storytype.repository.StoryTypeRepository;
import com.web.alpha.storytype.service.impl.StoryTypeServiceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class StoryTypeServiceImplTest {

	private StoryTypeRepository repository;
	private StoryTypeEventPublisher eventPublisher;
	private StoryTypeService service;

	@BeforeEach
	void setUp() {
		repository = mock(StoryTypeRepository.class);
		eventPublisher = mock(StoryTypeEventPublisher.class);
		service = new StoryTypeServiceImpl(repository, new StoryTypeMapper(), eventPublisher);
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
	void createUsesAuthenticatedUserAndPublishesCreatedEvent() {
		when(repository.save(any(StoryType.class))).thenAnswer(invocation -> {
			StoryType entity = invocation.getArgument(0);
			entity.setId(1L);
			return entity;
		});

		StoryTypeResponse response = service.create(
				new StoryTypeCreateRequest("Novel", LocalDate.of(2026, 7, 22), "Novel story type")
		);

		assertEquals(7L, response.createdBy());
		assertEquals(1, response.isActive());
		assertEquals(0, response.isDeleted());
		ArgumentCaptor<StoryTypeEvent> eventCaptor = ArgumentCaptor.forClass(StoryTypeEvent.class);
		verify(eventPublisher).publish(eventCaptor.capture());
		assertEquals(StoryTypeEventType.CREATED, eventCaptor.getValue().eventType());
		assertEquals(1L, eventCaptor.getValue().storyTypeId());
	}

	@Test
	void updateRefreshesAuditFieldsAndPublishesUpdatedEvent() {
		StoryType entity = storyType(1L);
		LocalDateTime previousCreatedAt = entity.getCreatedAt();
		when(repository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(entity);

		StoryTypeResponse response = service.update(
				1L,
				new StoryTypeUpdateRequest("Webtoon", null, "Updated", 0)
		);

		assertEquals(7L, response.createdBy());
		assertEquals(0, response.isActive());
		assertEquals(0, response.isDeleted());
		assertTrue(response.createdAt().isAfter(previousCreatedAt));
		ArgumentCaptor<StoryTypeEvent> eventCaptor = ArgumentCaptor.forClass(StoryTypeEvent.class);
		verify(eventPublisher).publish(eventCaptor.capture());
		assertEquals(StoryTypeEventType.UPDATED, eventCaptor.getValue().eventType());
	}

	@Test
	void deleteSoftDeletesAndPublishesDeletedEvent() {
		StoryType entity = storyType(1L);
		when(repository.findByIdAndIsDeleted(1L, 0)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(entity);

		service.delete(1L);

		assertEquals(1, entity.getIsDeleted());
		assertEquals(7L, entity.getCreatedBy());
		ArgumentCaptor<StoryTypeEvent> eventCaptor = ArgumentCaptor.forClass(StoryTypeEvent.class);
		verify(eventPublisher).publish(eventCaptor.capture());
		assertEquals(StoryTypeEventType.DELETED, eventCaptor.getValue().eventType());
	}

	private StoryType storyType(Long id) {
		StoryType entity = new StoryType();
		entity.setId(id);
		entity.setName("Novel");
		entity.setCreatedDate(LocalDate.of(2026, 7, 22));
		entity.setDescription("Novel story type");
		entity.setIsActive(1);
		entity.setIsDeleted(0);
		entity.setCreatedBy(1L);
		entity.setCreatedAt(LocalDateTime.of(2026, 7, 22, 10, 30));
		return entity;
	}
}
