package com.web.alpha.storytag.event;

import java.time.LocalDateTime;

public record StoryTagEvent(
        StoryTagEventType eventType,
        Long storyTagId,
        String name,
        Long performedBy,
        LocalDateTime occurredAt
) {
}