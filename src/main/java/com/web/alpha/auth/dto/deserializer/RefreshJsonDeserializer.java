package com.web.alpha.auth.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.auth.dto.AuthRefreshParam;
import java.io.IOException;
import java.util.Set;

public final class RefreshJsonDeserializer extends AuthJsonDeserializerSupport<AuthRefreshParam> {

	private static final Set<String> ALLOWED_FIELDS = Set.of("refreshToken");

	@Override
	public AuthRefreshParam deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectNode json = readObject(parser, context, AuthRefreshParam.class);
		rejectUnknownFields(json, ALLOWED_FIELDS, context, AuthRefreshParam.class);

		return new AuthRefreshParam(
				readText(json, "refreshToken", context, AuthRefreshParam.class)
		);
	}
}
