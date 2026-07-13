package com.web.alpha.appusers.mappers;

import com.web.alpha.appusers.domains.AppRole;
import com.web.alpha.appusers.domains.AppUser;
import com.web.alpha.auth.dto.AuthLoginResponse;
import com.web.alpha.auth.dto.AuthenticatedUserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

	@Mapping(target = "id", source = "user.id")
	@Mapping(target = "name", source = "user.name")
	@Mapping(target = "email", source = "user.email")
	@Mapping(target = "status", source = "user.status")
	@Mapping(target = "role", source = "role.name")
	AuthenticatedUserResponse toAuthenticatedUserResponse(AppUser user, AppRole role);

	default AuthLoginResponse toLoginResponse(
			String accessToken,
			String refreshToken,
			long expiresIn,
			long refreshExpiresIn,
			AppUser user,
			AppRole role
	) {
		return new AuthLoginResponse(
				"Bearer",
				accessToken,
				refreshToken,
				expiresIn,
				refreshExpiresIn,
				toAuthenticatedUserResponse(user, role)
		);
	}
}
