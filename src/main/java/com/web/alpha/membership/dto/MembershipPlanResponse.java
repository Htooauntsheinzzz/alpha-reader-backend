package com.web.alpha.membership.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MembershipPlanResponse (

        Long id,
        String planId,
        String name,
        BigDecimal price,
        Long duration,
        String description,
        Integer isActive,
        Integer isDeleted,
        Long createdBy,
        LocalDateTime createdAt,
        Long updateBy,
        LocalDateTime updateAt

){

}
