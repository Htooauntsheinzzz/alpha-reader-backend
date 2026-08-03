package com.web.alpha.storytag.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.storytag.dto.StoryTagUpdateRequest;
import java.io.IOException;
import java.util.Set;

public final class StoryTagUpdateRequestDeserializer
        extends StoryTagJsonDeserializerSupport<StoryTagUpdateRequest> {

    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "name",
            "createdDate",
            "description",
            "isActive"
    );

    @Override
    public StoryTagUpdateRequest deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectNode json = readObject(parser, context, StoryTagUpdateRequest.class);
        rejectUnknownFields(json, ALLOWED_FIELDS, context, StoryTagUpdateRequest.class);
        return new StoryTagUpdateRequest(
                readText(json, "name", context, StoryTagUpdateRequest.class),
                readDate(json, "createdDate", context, StoryTagUpdateRequest.class),
                readText(json, "description", context, StoryTagUpdateRequest.class),
                readInteger(json, "isActive", context, StoryTagUpdateRequest.class)
        );
    }
}