package com.web.alpha.common.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(RabbitMqProperties.class)
public class RabbitMqConfig {

	@Bean
	public TopicExchange domainEventExchange(RabbitMqProperties properties) {
		return new TopicExchange(properties.exchange(), true, false);
	}

	@Bean
	public DirectExchange deadLetterExchange(RabbitMqProperties properties) {
		return new DirectExchange(properties.deadLetterExchange(), true, false);
	}

	@Bean
	public Queue auditQueue(RabbitMqProperties properties) {
		return QueueBuilder.durable(properties.auditQueue())
				.deadLetterExchange(properties.deadLetterExchange())
				.deadLetterRoutingKey(properties.deadLetterRoutingKey())
				.build();
	}

	@Bean
	public Queue auditDeadLetterQueue(RabbitMqProperties properties) {
		return QueueBuilder.durable(properties.deadLetterQueue()).build();
	}

	@Bean
	public Binding auditQueueBinding(
			@Qualifier("auditQueue") Queue auditQueue,
			TopicExchange domainEventExchange
	) {
		return BindingBuilder.bind(auditQueue).to(domainEventExchange).with("#");
	}

	@Bean
	public Binding auditDeadLetterQueueBinding(
			@Qualifier("auditDeadLetterQueue") Queue auditDeadLetterQueue,
			DirectExchange deadLetterExchange,
			RabbitMqProperties properties
	) {
		return BindingBuilder.bind(auditDeadLetterQueue)
				.to(deadLetterExchange)
				.with(properties.deadLetterRoutingKey());
	}
}
