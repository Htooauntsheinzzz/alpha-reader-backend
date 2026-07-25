package com.web.alpha.storytype.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.storytype.dto.StoryTypeCreateRequest;
import java.io.IOException;
import java.util.Set;

public final class StoryTypeCreateRequestDeserializer
		extends StoryTypeJsonDeserializerSupport<StoryTypeCreateRequest> {

	private static final Set<String> ALLOWED_FIELDS = Set.of("name", "createdDate", "description");

	@Override
	public StoryTypeCreateRequest deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectNode json = readObject(parser, context, StoryTypeCreateRequest.class);
		rejectUnknownFields(json, ALLOWED_FIELDS, context, StoryTypeCreateRequest.class);
		return new StoryTypeCreateRequest(
				readText(json, "name", context, StoryTypeCreateRequest.class),
				readDate(json, "createdDate", context, StoryTypeCreateRequest.class),
				readText(json, "description", context, StoryTypeCreateRequest.class)
		);
	}
}
