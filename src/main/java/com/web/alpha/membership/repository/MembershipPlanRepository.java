package com.web.alpha.membership.repository;

import com.web.alpha.membership.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    Optional<MembershipPlan> findByIdAndIsDeleted(Long id, Integer isDeleted);

    boolean existsByNameIgnoreCaseAndIsDeleted(String name, Integer isDeleted);

    List<MembershipPlan> findAllByIsDeletedOrderByIdDesc(Integer isDeleted);
}
