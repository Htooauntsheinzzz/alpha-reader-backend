package com.web.alpha.common.messaging;

import java.util.Map;
import java.util.UUID;

public record DomainEvent(
		UUID eventId,
		String eventType,
		String aggregateType,
		String aggregateId,
		String occurredAt,
		String requestId,
		Long actorUserId,
		Map<String, Object> data
) {
}
