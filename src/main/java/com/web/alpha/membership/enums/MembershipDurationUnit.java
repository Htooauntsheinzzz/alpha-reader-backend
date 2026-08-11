package com.web.alpha.membership.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MembershipDurationUnit {
    DAY(100),
    MONTH(200),
    YEAR(300);

    private final int code;

    MembershipDurationUnit(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public static MembershipDurationUnit fromCode(int code) {
        for (MembershipDurationUnit unit : values()) {
            if (unit.code == code) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Duration unit must be 100 (DAY), 200 (MONTH), or 300 (YEAR)");
    }
}
