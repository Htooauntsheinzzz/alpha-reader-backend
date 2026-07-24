package com.web.alpha.auth.service;

public interface LoginRateLimitService {

	void checkAllowed(String email, String clientIp);

	void recordFailure(String email, String clientIp);

	void clearFailures(String email, String clientIp);
}
