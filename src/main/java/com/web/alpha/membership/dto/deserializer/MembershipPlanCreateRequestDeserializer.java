package com.web.alpha.membership.dto.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.membership.dto.MembershipCreateRequest;

import java.io.IOException;
import java.util.Set;

public final class MembershipPlanCreateRequestDeserializer extends MembershipPlanJsonDeserializerSupport<MembershipCreateRequest> {

    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "name",
            "price",
            "duration",
            "description",
            "durationUnit",
            "accessLevel",
            "isLifetime"
    );
    @Override
    public MembershipCreateRequest deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
        ObjectNode json = readObject(parser, context, MembershipCreateRequest.class);
        rejectUnknownFields(json, ALLOWED_FIELDS, context, MembershipCreateRequest.class);
        return new MembershipCreateRequest(
                readText(json, "name", context, MembershipCreateRequest.class),
                readDecimal(json, "price", context, MembershipCreateRequest.class),
                readLong(json, "duration", context, MembershipCreateRequest.class),
                readText(json, "description", context, MembershipCreateRequest.class),
                readDurationUnit(json, "durationUnit", context, MembershipCreateRequest.class),
                readInteger(json, "accessLevel", context, MembershipCreateRequest.class),
                readInteger(json, "isLifetime", context, MembershipCreateRequest.class)
        );
    }
}
