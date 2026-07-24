package com.web.alpha.appgenre.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.appgenre.dto.AppGenreUpdateRequest;
import java.io.IOException;
import java.util.Set;

public final class AppGenreUpdateRequestDeserializer
		extends AppGenreJsonDeserializerSupport<AppGenreUpdateRequest> {

	private static final Set<String> ALLOWED_FIELDS = Set.of(
			"name",
			"createdDate",
			"description",
			"isActive"
	);

	@Override
	public AppGenreUpdateRequest deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectNode json = readObject(parser, context, AppGenreUpdateRequest.class);
		rejectUnknownFields(json, ALLOWED_FIELDS, context, AppGenreUpdateRequest.class);

		return new AppGenreUpdateRequest(
				readText(json, "name", context, AppGenreUpdateRequest.class),
				readDate(json, "createdDate", context, AppGenreUpdateRequest.class),
				readText(json, "description", context, AppGenreUpdateRequest.class),
				readInteger(json, "isActive", context, AppGenreUpdateRequest.class)
		);
	}
}
