package com.web.alpha.auth.service;

import com.web.alpha.auth.dto.AuthLoginParam;
import com.web.alpha.auth.dto.AuthLoginResponse;
import com.web.alpha.auth.dto.AuthLogoutParam;
import com.web.alpha.auth.dto.AuthLogoutResponse;
import com.web.alpha.auth.dto.AuthRefreshParam;
import com.web.alpha.auth.dto.AuthRefreshResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public interface AuthService {

	AuthLoginResponse login(AuthLoginParam request, String clientIp);

	AuthRefreshResponse refresh(AuthRefreshParam request);

	AuthLogoutResponse logout(Jwt jwt, AuthLogoutParam request);
}
