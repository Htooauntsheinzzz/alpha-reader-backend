package com.web.alpha.storytype.event;

import java.time.LocalDateTime;

public record StoryTypeEvent(
		StoryTypeEventType eventType,
		Long storyTypeId,
		String name,
		Long performedBy,
		LocalDateTime occurredAt
) {
}
