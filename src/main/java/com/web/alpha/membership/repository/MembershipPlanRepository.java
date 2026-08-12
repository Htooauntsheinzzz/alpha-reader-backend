package com.web.alpha.membership.repository;

import com.web.alpha.membership.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    boolean existsByNameIgnoreCaseAndIsDeleted(String name, Integer isDeleted);

    List<MembershipPlan> findAllByIsDeletedOrderByIdDesc(Integer isDeleted);
}
