package com.web.alpha.membership.event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.web.alpha.common.outbox.service.OutboxService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class OutboxMembershipPlanEventPublisherTest {

    @Test
    void publishesLifetimeEventWithoutNullDurationValues() {
        OutboxService outboxService = mock(OutboxService.class);
        OutboxMembershipPlanEventPublisher publisher = new OutboxMembershipPlanEventPublisher(outboxService);

        publisher.publishCreated(new MembershipPlanEvent(
                1L,
                "PLN-001",
                "Free Plan",
                new BigDecimal("0.00"),
                null,
                null,
                0,
                1,
                7L,
                LocalDateTime.of(2026, 8, 11, 10, 30)
        ));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> dataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(outboxService).record(
                eq("MEMBERSHIP_PLAN_CREATED"),
                eq("MEMBERSHIP_PLAN"),
                eq("1"),
                eq("membership-plan.created"),
                eq(7L),
                dataCaptor.capture()
        );
        assertEquals(1, dataCaptor.getValue().get("isLifetime"));
        assertFalse(dataCaptor.getValue().containsKey("duration"));
        assertFalse(dataCaptor.getValue().containsKey("durationUnit"));
    }
}
