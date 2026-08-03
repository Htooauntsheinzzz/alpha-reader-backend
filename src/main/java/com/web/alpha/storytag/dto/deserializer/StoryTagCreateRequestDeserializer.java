package com.web.alpha.storytag.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.storytag.dto.StoryTagCreateRequest;
import java.io.IOException;
import java.util.Set;

public final class StoryTagCreateRequestDeserializer
        extends StoryTagJsonDeserializerSupport<StoryTagCreateRequest> {

    private static final Set<String> ALLOWED_FIELDS = Set.of("name", "createdDate", "description");

    @Override
    public StoryTagCreateRequest deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectNode json = readObject(parser, context, StoryTagCreateRequest.class);
        rejectUnknownFields(json, ALLOWED_FIELDS, context, StoryTagCreateRequest.class);
        return new StoryTagCreateRequest(
                readText(json, "name", context, StoryTagCreateRequest.class),
                readDate(json, "createdDate", context, StoryTagCreateRequest.class),
                readText(json, "description", context, StoryTagCreateRequest.class)
        );
    }
}