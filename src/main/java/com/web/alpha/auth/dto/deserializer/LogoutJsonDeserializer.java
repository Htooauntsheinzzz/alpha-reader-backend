package com.web.alpha.auth.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.web.alpha.auth.dto.AuthLogoutParam;
import java.io.IOException;
import java.util.Set;

public final class LogoutJsonDeserializer extends AuthJsonDeserializerSupport<AuthLogoutParam> {

	private static final Set<String> ALLOWED_FIELDS = Set.of("refreshToken");

	@Override
	public AuthLogoutParam deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectNode json = readObject(parser, context, AuthLogoutParam.class);
		rejectUnknownFields(json, ALLOWED_FIELDS, context, AuthLogoutParam.class);

		return new AuthLogoutParam(
				readText(json, "refreshToken", context, AuthLogoutParam.class)
		);
	}
}
