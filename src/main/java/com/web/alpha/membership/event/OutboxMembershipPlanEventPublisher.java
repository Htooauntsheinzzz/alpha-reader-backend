package com.web.alpha.membership.event;

import com.web.alpha.common.outbox.service.OutboxService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OutboxMembershipPlanEventPublisher implements MembershipPlanEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxMembershipPlanEventPublisher.class);

    private final OutboxService outboxService;

    public OutboxMembershipPlanEventPublisher(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    @Override
    public void publishCreated(MembershipPlanEvent event) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("planId", event.planId());
        data.put("name", event.name());
        data.put("price", event.price().toPlainString());
        data.put("accessLevel", event.accessLevel());
        data.put("isLifetime", event.isLifetime());
        if (event.duration() != null) {
            data.put("duration", event.duration());
        }
        if (event.durationUnit() != null) {
            data.put("durationUnit", event.durationUnit().getCode());
        }
        data.put("occurredAt", event.occurredAt().toString());

        outboxService.record(
                "MEMBERSHIP_PLAN_CREATED",
                "MEMBERSHIP_PLAN",
                event.membershipPlanId().toString(),
                "membership-plan.created",
                event.performedBy(),
                data
        );
        log.info(
                "Membership plan event recorded membershipPlanId={} planId={} performedBy={}",
                event.membershipPlanId(),
                event.planId(),
                event.performedBy()
        );
    }
}
