package com.web.alpha.common.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.alpha.common.messaging.DomainEvent;
import com.web.alpha.common.logging.CorrelationIdFilter;
import com.web.alpha.common.outbox.entity.OutboxEvent;
import com.web.alpha.common.outbox.entity.OutboxStatus;
import com.web.alpha.common.outbox.repository.OutboxEventRepository;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxService {

	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	public OutboxService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
		this.outboxEventRepository = outboxEventRepository;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public DomainEvent record(
			String eventType,
			String aggregateType,
			String aggregateId,
			String routingKey,
			Long actorUserId,
			Map<String, Object> data
	) {
		Instant occurredAt = Instant.now();
		DomainEvent event = new DomainEvent(
				UUID.randomUUID(),
				eventType,
				aggregateType,
				aggregateId,
				occurredAt.toString(),
				MDC.get(CorrelationIdFilter.MDC_KEY),
				actorUserId,
				Map.copyOf(data)
		);

		OutboxEvent outboxEvent = new OutboxEvent();
		outboxEvent.setId(event.eventId());
		outboxEvent.setEventType(event.eventType());
		outboxEvent.setAggregateType(event.aggregateType());
		outboxEvent.setAggregateId(event.aggregateId());
		outboxEvent.setRoutingKey(routingKey);
		outboxEvent.setPayload(serialize(event));
		outboxEvent.setStatus(OutboxStatus.PENDING);
		outboxEvent.setRetryCount(0);
		outboxEvent.setCreatedAt(occurredAt);
		outboxEventRepository.save(outboxEvent);
		return event;
	}

	private String serialize(DomainEvent event) {
		try {
			return objectMapper.writeValueAsString(event);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Could not serialize outbox event", exception);
		}
	}
}
