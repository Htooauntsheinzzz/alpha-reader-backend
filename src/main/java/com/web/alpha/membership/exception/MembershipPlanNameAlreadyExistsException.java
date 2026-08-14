package com.web.alpha.membership.exception;

import com.web.alpha.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public final class MembershipPlanNameAlreadyExistsException extends ApiException {

    public MembershipPlanNameAlreadyExistsException() {
        super(
                HttpStatus.CONFLICT,
                "MEMBERSHIP_PLAN_NAME_ALREADY_EXISTS",
                "Membership plan name already exists"
        );
    }

    public MembershipPlanNameAlreadyExistsException(Throwable cause) {
        super(
                HttpStatus.CONFLICT,
                "MEMBERSHIP_PLAN_NAME_ALREADY_EXISTS",
                "Membership plan name already exists",
                cause
        );
    }


}
