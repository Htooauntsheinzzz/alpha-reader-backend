package com.web.alpha.auth.service;

import com.web.alpha.appusers.domains.AppRole;
import com.web.alpha.appusers.domains.AppUser;

public interface JwtTokenService {

	String generateAccessToken(AppUser user, AppRole role);

	long getAccessTokenExpiresInSeconds();
}
