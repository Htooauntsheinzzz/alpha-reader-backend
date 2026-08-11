package com.web.alpha.membership.dto;

import com.web.alpha.membership.enums.MembershipDurationUnit;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MembershipPlanResponse (

        Long id,
        String planId,
        String name,
        BigDecimal price,
        Long duration,
        String description,
        MembershipDurationUnit durationUnit,
        Integer accessLevel,
        Integer isLifetime,
        Integer isActive,
        Integer isDeleted,
        Long createdBy,
        LocalDateTime createdAt,
        Long updateBy,
        LocalDateTime updateAt

){

}
