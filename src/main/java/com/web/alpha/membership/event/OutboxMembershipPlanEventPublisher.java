package com.web.alpha.membership.event;

import com.web.alpha.common.outbox.service.OutboxService;
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
        outboxService.record(
                "MEMBERSHIP_PLAN_CREATED",
                "MEMBERSHIP_PLAN",
                event.membershipPlanId().toString(),
                "membership-plan.created",
                event.performedBy(),
                Map.of(
                        "planId", event.planId(),
                        "name", event.name(),
                        "price", event.price().toPlainString(),
                        "duration", event.duration(),
                        "occurredAt", event.occurredAt().toString()
                )
        );
        log.info(
                "Membership plan event recorded membershipPlanId={} planId={} performedBy={}",
                event.membershipPlanId(),
                event.planId(),
                event.performedBy()
        );
    }
}
