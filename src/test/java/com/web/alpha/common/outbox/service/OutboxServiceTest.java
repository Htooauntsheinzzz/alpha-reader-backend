package com.web.alpha.common.outbox.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.alpha.common.messaging.DomainEvent;
import com.web.alpha.common.outbox.entity.OutboxEvent;
import com.web.alpha.common.outbox.entity.OutboxStatus;
import com.web.alpha.common.outbox.repository.OutboxEventRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class OutboxServiceTest {

	@Test
	void recordsPendingEventWithSerializedPayload() throws Exception {
		OutboxEventRepository repository = mock(OutboxEventRepository.class);
		when(repository.save(any(OutboxEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));
		ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
		OutboxService service = new OutboxService(repository, objectMapper);

		DomainEvent event = service.record(
				"APP_GENRE_CREATED",
				"APP_GENRE",
				"12",
				"app-genre.created",
				1L,
				Map.of("name", "Fantasy")
		);

		ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
		verify(repository).save(captor.capture());
		OutboxEvent outboxEvent = captor.getValue();
		assertEquals(event.eventId(), outboxEvent.getId());
		assertEquals(OutboxStatus.PENDING, outboxEvent.getStatus());
		assertEquals(0, outboxEvent.getRetryCount());
		assertEquals("app-genre.created", outboxEvent.getRoutingKey());
		assertNotNull(outboxEvent.getCreatedAt());
		assertEquals(
				"APP_GENRE_CREATED",
				objectMapper.readTree(outboxEvent.getPayload()).get("eventType").asText()
		);
	}
}
