package com.web.alpha.appusers.repositories;

import com.web.alpha.appusers.domains.AppUserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppUserRoleRepository extends JpaRepository<AppUserRole, Long> {

	@Query("""
			select userRole
			from AppUserRole userRole
			join fetch userRole.user
			join fetch userRole.role
			where userRole.user.id = :userId
			""")
	Optional<AppUserRole> findWithUserAndRoleByUserId(@Param("userId") Long userId);
}
