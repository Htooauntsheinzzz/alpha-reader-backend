package com.web.alpha.auth.controller;

import com.web.alpha.auth.dto.AuthLoginParam;
import com.web.alpha.auth.dto.AuthLoginResponse;
import com.web.alpha.auth.dto.AuthLogoutParam;
import com.web.alpha.auth.dto.AuthLogoutResponse;
import com.web.alpha.auth.dto.AuthRefreshParam;
import com.web.alpha.auth.dto.AuthRefreshResponse;
import com.web.alpha.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthLoginResponse> login(
			@Valid @RequestBody AuthLoginParam request,
			HttpServletRequest httpServletRequest
	) {
		return ResponseEntity.ok(authService.login(request, getClientIp(httpServletRequest)));
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthRefreshResponse> refresh(@Valid @RequestBody AuthRefreshParam request) {
		return ResponseEntity.ok(authService.refresh(request));
	}

	@PostMapping("/logout")
	public ResponseEntity<AuthLogoutResponse> logout(
			@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody(required = false) AuthLogoutParam request
	) {
		return ResponseEntity.ok(authService.logout(jwt, request));
	}

	private String getClientIp(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (forwardedFor != null && !forwardedFor.isBlank()) {
			return forwardedFor.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}
}
