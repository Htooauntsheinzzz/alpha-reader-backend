package com.web.alpha.membership.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.web.alpha.membership.dto.deserializer.MembershipPlanCreateRequestDeserializer;
import com.web.alpha.membership.enums.MembershipDurationUnit;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonDeserialize(using = MembershipPlanCreateRequestDeserializer.class)
public record MembershipCreateRequest(
        @NotBlank
        @Size(max = 300)
        String name,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal price,

        @Positive
        Long duration,

        @Size(max = 500)
        String description,

        MembershipDurationUnit durationUnit,

        @NotNull
        @Min(0)
        Integer accessLevel,

        @NotNull
        @Min(0)
        @Max(1)
        Integer isLifetime
) implements Serializable {

    @JsonIgnore
    @AssertTrue(message = "Lifetime plans require null duration and durationUnit; non-lifetime plans require both")
    public boolean isDurationConfigurationValid() {
        if (isLifetime == null || (isLifetime != 0 && isLifetime != 1)) {
            return true;
        }
        if (isLifetime == 1) {
            return duration == null && durationUnit == null;
        }
        return duration != null && duration > 0 && durationUnit != null;
    }
}
