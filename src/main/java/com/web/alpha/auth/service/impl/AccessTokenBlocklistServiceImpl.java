package com.web.alpha.auth.service.impl;

import com.web.alpha.auth.service.AccessTokenBlocklistService;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenBlocklistServiceImpl implements AccessTokenBlocklistService {

	private static final String KEY_PREFIX = "admin:access-token:blocklist:";

	private final StringRedisTemplate redisTemplate;

	public AccessTokenBlocklistServiceImpl(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void blocklist(String tokenId, Duration ttl) {
		if (tokenId != null && !tokenId.isBlank() && !ttl.isNegative() && !ttl.isZero()) {
			redisTemplate.opsForValue().set(key(tokenId), "revoked", ttl);
		}
	}

	@Override
	public boolean isBlocklisted(String tokenId) {
		return tokenId != null && Boolean.TRUE.equals(redisTemplate.hasKey(key(tokenId)));
	}

	private String key(String tokenId) {
		return KEY_PREFIX + tokenId;
	}
}
