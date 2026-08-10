package com.web.alpha.membership.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MembershipPlanEvent(
        Long membershipPlanId,
        String planId,
        String name,
        BigDecimal price,
        Long duration,
        Long performedBy,
        LocalDateTime occurredAt
) {
}
