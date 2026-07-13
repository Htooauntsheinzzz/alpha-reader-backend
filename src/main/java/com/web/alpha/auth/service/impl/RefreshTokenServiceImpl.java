package com.web.alpha.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.alpha.appusers.domains.AppRole;
import com.web.alpha.appusers.domains.AppUser;
import com.web.alpha.auth.service.RefreshTokenData;
import com.web.alpha.auth.service.RefreshTokenService;
import com.web.alpha.config.JwtProperties;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private static final String KEY_PREFIX = "admin:refresh-token:";
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;
	private final JwtProperties jwtProperties;

	public RefreshTokenServiceImpl(
			StringRedisTemplate redisTemplate,
			ObjectMapper objectMapper,
			JwtProperties jwtProperties
	) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
		this.jwtProperties = jwtProperties;
	}

	@Override
	public String create(AppUser user, AppRole role) {
		String refreshToken = generateToken();
		RefreshTokenData data = new RefreshTokenData(
				user.getId(),
				user.getEmail(),
				role.getName(),
				Instant.now().getEpochSecond()
		);
		redisTemplate.opsForValue().set(key(refreshToken), write(data), refreshTokenTtl());
		return refreshToken;
	}

	@Override
	public Optional<RefreshTokenData> find(String refreshToken) {
		String json = redisTemplate.opsForValue().get(key(refreshToken));
		if (json == null) {
			return Optional.empty();
		}
		try {
			return Optional.of(objectMapper.readValue(json, RefreshTokenData.class));
		} catch (JsonProcessingException exception) {
			redisTemplate.delete(key(refreshToken));
			return Optional.empty();
		}
	}

	@Override
	public void delete(String refreshToken) {
		if (refreshToken != null && !refreshToken.isBlank()) {
			redisTemplate.delete(key(refreshToken));
		}
	}

	@Override
	public long getRefreshTokenExpiresInSeconds() {
		return refreshTokenTtl().toSeconds();
	}

	private String write(RefreshTokenData data) {
		try {
			return objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("Unable to write refresh token data", exception);
		}
	}

	private Duration refreshTokenTtl() {
		return Duration.ofDays(jwtProperties.refreshTokenExpirationDays());
	}

	private String key(String refreshToken) {
		return KEY_PREFIX + refreshToken;
	}

	private String generateToken() {
		byte[] bytes = new byte[32];
		SECURE_RANDOM.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}
