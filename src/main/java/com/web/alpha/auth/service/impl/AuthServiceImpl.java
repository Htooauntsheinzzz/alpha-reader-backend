package com.web.alpha.auth.service.impl;

import com.web.alpha.appusers.domains.AppRole;
import com.web.alpha.appusers.domains.AppUser;
import com.web.alpha.appusers.repositories.AppUserRepository;
import com.web.alpha.appusers.repositories.AppUserRoleRepository;
import com.web.alpha.auth.dto.AuthLoginParam;
import com.web.alpha.auth.dto.AuthLoginResponse;
import com.web.alpha.auth.dto.AuthLogoutParam;
import com.web.alpha.auth.dto.AuthLogoutResponse;
import com.web.alpha.auth.dto.AuthRefreshParam;
import com.web.alpha.auth.dto.AuthRefreshResponse;
import com.web.alpha.auth.exception.AccountInactiveException;
import com.web.alpha.auth.exception.AuthenticationRequiredException;
import com.web.alpha.auth.exception.InvalidCredentialsException;
import com.web.alpha.auth.exception.InvalidRefreshTokenException;
import com.web.alpha.auth.exception.RoleNotAssignedException;
import com.web.alpha.auth.mappers.AuthMapper;
import com.web.alpha.auth.service.AccessTokenBlocklistService;
import com.web.alpha.auth.service.AuthService;
import com.web.alpha.auth.service.JwtTokenService;
import com.web.alpha.auth.service.LoginRateLimitService;
import com.web.alpha.auth.service.RefreshTokenData;
import com.web.alpha.auth.service.RefreshTokenService;
import com.web.alpha.common.outbox.service.OutboxService;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
	private static final String ACTIVE_STATUS = "ACTIVE";

	private final AppUserRepository appUserRepository;
	private final AppUserRoleRepository appUserRoleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenService jwtTokenService;
	private final RefreshTokenService refreshTokenService;
	private final LoginRateLimitService loginRateLimitService;
	private final AccessTokenBlocklistService accessTokenBlocklistService;
	private final AuthMapper authMapper;
	private final OutboxService outboxService;

	public AuthServiceImpl(
			AppUserRepository appUserRepository,
			AppUserRoleRepository appUserRoleRepository,
			PasswordEncoder passwordEncoder,
			JwtTokenService jwtTokenService,
			RefreshTokenService refreshTokenService,
			LoginRateLimitService loginRateLimitService,
			AccessTokenBlocklistService accessTokenBlocklistService,
			AuthMapper authMapper,
			OutboxService outboxService
	) {
		this.appUserRepository = appUserRepository;
		this.appUserRoleRepository = appUserRoleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenService = jwtTokenService;
		this.refreshTokenService = refreshTokenService;
		this.loginRateLimitService = loginRateLimitService;
		this.accessTokenBlocklistService = accessTokenBlocklistService;
		this.authMapper = authMapper;
		this.outboxService = outboxService;
	}

	@Override
	@Transactional
	public AuthLoginResponse login(AuthLoginParam request, String clientIp) {
		String email = request.email().trim();
		log.info("Admin login attempt email={} clientIp={}", email, clientIp);
		loginRateLimitService.checkAllowed(email, clientIp);

		AppUser user = appUserRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> {
					loginRateLimitService.recordFailure(email, clientIp);
					log.warn("Admin login failed reason=invalid_credentials email={} clientIp={}", email, clientIp);
					return invalidCredentials();
				});

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			loginRateLimitService.recordFailure(email, clientIp);
			log.warn("Admin login failed reason=invalid_credentials email={} clientIp={}", email, clientIp);
			throw invalidCredentials();
		}

		if (!ACTIVE_STATUS.equalsIgnoreCase(user.getStatus())) {
			log.warn("Admin login failed reason=inactive_account userId={} clientIp={}", user.getId(), clientIp);
			throw new AccountInactiveException();
		}

		AppRole role = appUserRoleRepository.findWithUserAndRoleByUserId(user.getId())
				.orElseThrow(() -> {
					log.warn("Admin login failed reason=role_not_assigned userId={} clientIp={}", user.getId(), clientIp);
					return new RoleNotAssignedException();
				})
				.getRole();

		String accessToken = jwtTokenService.generateAccessToken(user, role);
		String refreshToken = refreshTokenService.create(user, role);
		loginRateLimitService.clearFailures(email, clientIp);
		outboxService.record(
				"AUTH_LOGIN_SUCCEEDED",
				"APP_USER",
				user.getId().toString(),
				"auth.login-succeeded",
				user.getId(),
				Map.of("role", role.getName(), "clientIp", clientIp)
		);
		log.info("Admin login succeeded userId={} role={} clientIp={}", user.getId(), role.getName(), clientIp);

		return authMapper.toLoginResponse(
				accessToken,
				refreshToken,
				jwtTokenService.getAccessTokenExpiresInSeconds(),
				refreshTokenService.getRefreshTokenExpiresInSeconds(),
				user,
				role
		);
	}

	@Override
	@Transactional(readOnly = true)
	public AuthRefreshResponse refresh(AuthRefreshParam request) {
		RefreshTokenData refreshTokenData = refreshTokenService.find(request.refreshToken())
				.orElseThrow(InvalidRefreshTokenException::new);

		AppUser user = appUserRepository.findById(refreshTokenData.userId())
				.orElseThrow(InvalidRefreshTokenException::new);

		if (!ACTIVE_STATUS.equalsIgnoreCase(user.getStatus())) {
			refreshTokenService.delete(request.refreshToken());
			throw new AccountInactiveException();
		}

		AppRole role = appUserRoleRepository.findWithUserAndRoleByUserId(user.getId())
				.orElseThrow(RoleNotAssignedException::new)
				.getRole();

		String accessToken = jwtTokenService.generateAccessToken(user, role);
		return new AuthRefreshResponse(
				"Bearer",
				accessToken,
				jwtTokenService.getAccessTokenExpiresInSeconds()
		);
	}

	@Override
	public AuthLogoutResponse logout(Jwt jwt, AuthLogoutParam request) {
		if (jwt == null) {
			throw new AuthenticationRequiredException();
		}

		Instant expiresAt = jwt.getExpiresAt();
		if (jwt.getId() != null && expiresAt != null) {
			Duration ttl = Duration.between(Instant.now(), expiresAt);
			accessTokenBlocklistService.blocklist(jwt.getId(), ttl);
		}

		if (request != null) {
			refreshTokenService.delete(request.refreshToken());
		}
		log.info("Admin logout succeeded userId={}", jwt.getSubject());

		return new AuthLogoutResponse("Admin-web Logged out successfully");
	}

	private InvalidCredentialsException invalidCredentials() {
		return new InvalidCredentialsException();
	}
}
