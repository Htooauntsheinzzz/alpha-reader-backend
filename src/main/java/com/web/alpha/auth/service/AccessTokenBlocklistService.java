package com.web.alpha.auth.service;

import java.time.Duration;

public interface AccessTokenBlocklistService {

	void blocklist(String tokenId, Duration ttl);

	boolean isBlocklisted(String tokenId);
}
