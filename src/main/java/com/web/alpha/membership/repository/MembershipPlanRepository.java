package com.web.alpha.membership.repository;

import com.web.alpha.membership.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    boolean existsByNameIgnoreCaseAndIsDeleted(String name, Integer isDeleted);
}
