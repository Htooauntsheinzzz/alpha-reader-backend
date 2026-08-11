package com.web.alpha.membership.event;

import com.web.alpha.membership.enums.MembershipDurationUnit;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MembershipPlanEvent(
        Long membershipPlanId,
        String planId,
        String name,
        BigDecimal price,
        Long duration,
        MembershipDurationUnit durationUnit,
        Integer accessLevel,
        Integer isLifetime,
        Long performedBy,
        LocalDateTime occurredAt
) {
}
