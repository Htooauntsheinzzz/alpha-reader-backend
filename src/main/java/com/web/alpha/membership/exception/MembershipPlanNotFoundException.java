package com.web.alpha.membership.exception;

import com.web.alpha.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class MembershipPlanNotFoundException extends ApiException {
    public MembershipPlanNotFoundException() {
        super(HttpStatus.NOT_FOUND,"MEMBERSHIP_PLAN_NOT_FOUND", "Membership plan not found");
    }
}
