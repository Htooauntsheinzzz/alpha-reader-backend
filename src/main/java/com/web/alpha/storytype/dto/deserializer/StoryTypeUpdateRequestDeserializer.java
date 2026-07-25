package com.web.alpha.storytype.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.storytype.dto.StoryTypeUpdateRequest;
import java.io.IOException;
import java.util.Set;

public final class StoryTypeUpdateRequestDeserializer
		extends StoryTypeJsonDeserializerSupport<StoryTypeUpdateRequest> {

	private static final Set<String> ALLOWED_FIELDS = Set.of(
			"name",
			"createdDate",
			"description",
			"isActive"
	);

	@Override
	public StoryTypeUpdateRequest deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectNode json = readObject(parser, context, StoryTypeUpdateRequest.class);
		rejectUnknownFields(json, ALLOWED_FIELDS, context, StoryTypeUpdateRequest.class);
		return new StoryTypeUpdateRequest(
				readText(json, "name", context, StoryTypeUpdateRequest.class),
				readDate(json, "createdDate", context, StoryTypeUpdateRequest.class),
				readText(json, "description", context, StoryTypeUpdateRequest.class),
				readInteger(json, "isActive", context, StoryTypeUpdateRequest.class)
		);
	}
}
