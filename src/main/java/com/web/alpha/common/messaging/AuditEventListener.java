package com.web.alpha.common.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AuditEventListener {

	private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);

	private final ObjectMapper objectMapper;

	public AuditEventListener(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@RabbitListener(queues = "${app.messaging.audit-queue}")
	public void consume(byte[] payload) throws IOException {
		JsonNode event = objectMapper.readTree(payload);
		log.info(
				"Audit event received eventId={} eventType={} aggregateType={} aggregateId={} actorUserId={} sourceRequestId={}",
				text(event, "eventId"),
				text(event, "eventType"),
				text(event, "aggregateType"),
				text(event, "aggregateId"),
				text(event, "actorUserId"),
				text(event, "requestId")
		);
	}

	private String text(JsonNode event, String fieldName) {
		JsonNode value = event.get(fieldName);
		return value == null || value.isNull() ? "none" : value.asText();
	}
}
