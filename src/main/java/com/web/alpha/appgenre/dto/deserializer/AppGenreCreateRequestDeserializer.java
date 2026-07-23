package com.web.alpha.appgenre.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.appgenre.dto.AppGenreCreateRequest;
import java.io.IOException;
import java.util.Set;

public final class AppGenreCreateRequestDeserializer
		extends AppGenreJsonDeserializerSupport<AppGenreCreateRequest> {

	private static final Set<String> ALLOWED_FIELDS = Set.of("name", "createdDate", "description");

	@Override
	public AppGenreCreateRequest deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectNode json = readObject(parser, context, AppGenreCreateRequest.class);
		rejectUnknownFields(json, ALLOWED_FIELDS, context, AppGenreCreateRequest.class);

		return new AppGenreCreateRequest(
				readText(json, "name", context, AppGenreCreateRequest.class),
				readDate(json, "createdDate", context, AppGenreCreateRequest.class),
				readText(json, "description", context, AppGenreCreateRequest.class)
		);
	}
}
