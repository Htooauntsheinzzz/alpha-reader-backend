package com.web.alpha.appusers.repositories;

import com.web.alpha.appusers.domains.AppRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {

	Optional<AppRole> findByName(String name);
}
