package com.web.alpha.auth.service.impl;

import com.web.alpha.auth.exception.LoginRateLimitExceededException;
import com.web.alpha.auth.service.LoginRateLimitService;
import java.time.Duration;
import java.util.Locale;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoginRateLimitServiceImpl implements LoginRateLimitService {

	private static final int MAX_FAILED_ATTEMPTS = 5;
	private static final Duration WINDOW = Duration.ofMinutes(15);
	private static final String KEY_PREFIX = "admin:login:attempts:";

	private final StringRedisTemplate redisTemplate;

	public LoginRateLimitServiceImpl(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void checkAllowed(String email, String clientIp) {
		String value = redisTemplate.opsForValue().get(key(email, clientIp));
		if (value != null && Integer.parseInt(value) >= MAX_FAILED_ATTEMPTS) {
			throw new LoginRateLimitExceededException();
		}
	}

	@Override
	public void recordFailure(String email, String clientIp) {
		String key = key(email, clientIp);
		Long attempts = redisTemplate.opsForValue().increment(key);
		if (attempts != null && attempts == 1L) {
			redisTemplate.expire(key, WINDOW);
		}
	}

	@Override
	public void clearFailures(String email, String clientIp) {
		redisTemplate.delete(key(email, clientIp));
	}

	private String key(String email, String clientIp) {
		String normalizedEmail = email == null ? "unknown" : email.trim().toLowerCase(Locale.ROOT);
		String normalizedIp = clientIp == null || clientIp.isBlank() ? "unknown" : clientIp.trim();
		return KEY_PREFIX + normalizedEmail + ":" + normalizedIp;
	}
}
