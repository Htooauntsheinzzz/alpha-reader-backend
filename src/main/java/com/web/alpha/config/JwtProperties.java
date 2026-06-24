package com.web.alpha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
		String issuer,
		long accessTokenExpirationMinutes,
		long refreshTokenExpirationDays,
		String privateKeyPath,
		String publicKeyPath
) {
}
