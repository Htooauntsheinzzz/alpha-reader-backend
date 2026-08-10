package com.web.alpha.membership.event;

public interface MembershipPlanEventPublisher {

    void publishCreated(MembershipPlanEvent event);
}
