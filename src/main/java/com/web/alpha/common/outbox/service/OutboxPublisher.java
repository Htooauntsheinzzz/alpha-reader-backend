package com.web.alpha.common.outbox.service;

import com.web.alpha.common.messaging.RabbitMqProperties;
import com.web.alpha.common.outbox.entity.OutboxEvent;
import com.web.alpha.common.outbox.entity.OutboxStatus;
import com.web.alpha.common.outbox.repository.OutboxEventRepository;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxPublisher {

	private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
	private static final int MAX_ERROR_LENGTH = 1000;

	private final OutboxEventRepository outboxEventRepository;
	private final RabbitTemplate rabbitTemplate;
	private final RabbitMqProperties properties;

	public OutboxPublisher(
			OutboxEventRepository outboxEventRepository,
			RabbitTemplate rabbitTemplate,
			RabbitMqProperties properties
	) {
		this.outboxEventRepository = outboxEventRepository;
		this.rabbitTemplate = rabbitTemplate;
		this.properties = properties;
	}

	@Scheduled(fixedDelayString = "${app.messaging.publisher-fixed-delay-millis:5000}")
	@Transactional
	public void publishPendingEvents() {
		for (OutboxEvent event : outboxEventRepository.findPendingBatchForUpdate(properties.publisherBatchSize())) {
			try {
				publish(event);
				event.setStatus(OutboxStatus.PUBLISHED);
				event.setPublishedAt(Instant.now());
				event.setLastError(null);
				log.info(
						"Outbox event published eventId={} eventType={} routingKey={}",
						event.getId(),
						event.getEventType(),
						event.getRoutingKey()
				);
			} catch (Exception exception) {
				markFailedAttempt(event, exception);
				break;
			}
		}
	}

	private void publish(OutboxEvent event) throws Exception {
		CorrelationData correlationData = new CorrelationData(event.getId().toString());
		rabbitTemplate.convertAndSend(
				properties.exchange(),
				event.getRoutingKey(),
				event.getPayload(),
				message -> {
					message.getMessageProperties().setContentType("application/json");
					message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
					message.getMessageProperties().setMessageId(event.getId().toString());
					message.getMessageProperties().setHeader("eventType", event.getEventType());
					return message;
				},
				correlationData
		);

		CorrelationData.Confirm confirm = correlationData.getFuture().get(
				properties.publisherConfirmTimeoutMillis(),
				TimeUnit.MILLISECONDS
		);
		if (!confirm.ack()) {
			throw new IllegalStateException("RabbitMQ rejected message: " + confirm.reason());
		}
		if (correlationData.getReturned() != null) {
			throw new IllegalStateException("RabbitMQ returned unroutable message");
		}
	}

	private void markFailedAttempt(OutboxEvent event, Exception exception) {
		int retryCount = event.getRetryCount() + 1;
		event.setRetryCount(retryCount);
		event.setLastError(truncate(exception.getMessage()));
		if (retryCount >= properties.publisherMaxRetries()) {
			event.setStatus(OutboxStatus.FAILED);
		}
		log.warn(
				"Outbox publish failed eventId={} retryCount={} status={} reason={}",
				event.getId(),
				retryCount,
				event.getStatus(),
				event.getLastError()
		);
	}

	private String truncate(String message) {
		String value = message == null ? "Unknown RabbitMQ publishing error" : message;
		return value.length() <= MAX_ERROR_LENGTH ? value : value.substring(0, MAX_ERROR_LENGTH);
	}
}
