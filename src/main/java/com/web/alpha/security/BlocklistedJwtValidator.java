package com.web.alpha.security;

import com.web.alpha.auth.service.AccessTokenBlocklistService;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class BlocklistedJwtValidator implements OAuth2TokenValidator<Jwt> {

	private static final OAuth2Error BLOCKLISTED_TOKEN_ERROR = new OAuth2Error(
			"invalid_token",
			"JWT has been revoked",
			null
	);

	private final AccessTokenBlocklistService accessTokenBlocklistService;

	public BlocklistedJwtValidator(AccessTokenBlocklistService accessTokenBlocklistService) {
		this.accessTokenBlocklistService = accessTokenBlocklistService;
	}

	@Override
	public OAuth2TokenValidatorResult validate(Jwt token) {
		if (accessTokenBlocklistService.isBlocklisted(token.getId())) {
			return OAuth2TokenValidatorResult.failure(BLOCKLISTED_TOKEN_ERROR);
		}
		return OAuth2TokenValidatorResult.success();
	}
}
