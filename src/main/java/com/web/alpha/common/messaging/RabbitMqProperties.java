package com.web.alpha.common.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging")
public record RabbitMqProperties(
		String exchange,
		String auditQueue,
		String storyTypeQueue,
		String membershipPlanQueue,
		String deadLetterExchange,
		String deadLetterQueue,
		String deadLetterRoutingKey,
		int publisherBatchSize,
		int publisherMaxRetries,
		long publisherConfirmTimeoutMillis
) {
}
