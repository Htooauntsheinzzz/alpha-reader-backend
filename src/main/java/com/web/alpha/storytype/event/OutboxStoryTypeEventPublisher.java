package com.web.alpha.storytype.event;

import com.web.alpha.common.outbox.service.OutboxService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OutboxStoryTypeEventPublisher implements StoryTypeEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(OutboxStoryTypeEventPublisher.class);
	private static final String AGGREGATE_TYPE = "STORY_TYPE";

	private final OutboxService outboxService;

	public OutboxStoryTypeEventPublisher(OutboxService outboxService) {
		this.outboxService = outboxService;
	}

	@Override
	public void publish(StoryTypeEvent event) {
		String routingKey = switch (event.eventType()) {
			case CREATED -> "story-type.created";
			case UPDATED -> "story-type.updated";
			case DELETED -> "story-type.deleted";
		};
		outboxService.record(
				"STORY_TYPE_" + event.eventType().name(),
				AGGREGATE_TYPE,
				event.storyTypeId().toString(),
				routingKey,
				event.performedBy(),
				Map.of(
						"name", event.name(),
						"occurredAt", event.occurredAt().toString()
				)
		);
		log.info(
				"Story type event recorded eventType={} storyTypeId={} performedBy={}",
				event.eventType(),
				event.storyTypeId(),
				event.performedBy()
		);
	}
}
