package com.web.alpha.storytag.event;

import com.web.alpha.common.outbox.service.OutboxService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OutboxStoryTagEventPublisher implements StoryTagEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxStoryTagEventPublisher.class);
    private static final String AGGREGATE_TYPE = "STORY_TAG";

    private final OutboxService outboxService;

    public OutboxStoryTagEventPublisher(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    @Override
    public void publish(StoryTagEvent event) {
        String routingKey = switch (event.eventType()) {
            case CREATED -> "story-tag.created";
            case UPDATED -> "story-tag.updated";
            case DELETED -> "story-tag.deleted";
        };

        outboxService.record(
                "STORY_TAG_" + event.eventType().name(),
                AGGREGATE_TYPE,
                event.storyTagId().toString(),
                routingKey,
                event.performedBy(),
                Map.of(
                        "name", event.name(),
                        "occurredAt", event.occurredAt().toString()
                )
        );

        log.info(
                "Story tag event recorded eventType={} storyTagId={} performedBy={}",
                event.eventType(),
                event.storyTagId(),
                event.performedBy()
        );
    }
}