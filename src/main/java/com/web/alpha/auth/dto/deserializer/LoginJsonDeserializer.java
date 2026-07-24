package com.web.alpha.auth.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.auth.dto.AuthLoginParam;
import java.io.IOException;
import java.util.Set;

public final class LoginJsonDeserializer extends AuthJsonDeserializerSupport<AuthLoginParam> {

	private static final Set<String> ALLOWED_FIELDS = Set.of("email", "password");

	@Override
	public AuthLoginParam deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectNode json = readObject(parser, context, AuthLoginParam.class);
		rejectUnknownFields(json, ALLOWED_FIELDS, context, AuthLoginParam.class);

		return new AuthLoginParam(
				readText(json, "email", context, AuthLoginParam.class),
				readText(json, "password", context, AuthLoginParam.class)
		);
	}
}
