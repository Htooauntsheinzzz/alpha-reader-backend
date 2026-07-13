package com.web.alpha.auth.service;

import com.web.alpha.appusers.domains.AppRole;
import com.web.alpha.appusers.domains.AppUser;
import java.util.Optional;

public interface RefreshTokenService {

	String create(AppUser user, AppRole role);

	Optional<RefreshTokenData> find(String refreshToken);

	void delete(String refreshToken);

	long getRefreshTokenExpiresInSeconds();
}
