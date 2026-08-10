package com.web.alpha.membership.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.web.alpha.membership.dto.deserializer.MembershipPlanCreateRequestDeserializer;
import jakarta.validation.constraints.DecimalMin;
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
        @DecimalMin(value = "0.00", inclusive = false)
        BigDecimal price,

        @NotNull
        @Positive
        Long duration,

        @Size(max = 500)
        String description
) implements Serializable {
}
