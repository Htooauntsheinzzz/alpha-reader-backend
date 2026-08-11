package com.web.alpha.membership.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import com.web.alpha.membership.enums.MembershipDurationUnit;
import org.junit.jupiter.api.Test;

class MembershipCreateRequestDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void deserializesValidBusinessFields() throws JsonProcessingException {
        MembershipCreateRequest request = objectMapper.readValue(
                "{\"name\":\"Monthly Plan\",\"price\":9.99,\"duration\":1,\"description\":\"Monthly membership\",\"durationUnit\":200,\"accessLevel\":10,\"isLifetime\":0}",
                MembershipCreateRequest.class
        );

        assertEquals("Monthly Plan", request.name());
        assertEquals(new BigDecimal("9.99"), request.price());
        assertEquals(1L, request.duration());
        assertEquals(MembershipDurationUnit.MONTH, request.durationUnit());
        assertEquals(10, request.accessLevel());
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void deserializesLifetimeFreePlan() throws JsonProcessingException {
        MembershipCreateRequest request = objectMapper.readValue(
                "{\"name\":\"Free Plan\",\"price\":0.00,\"duration\":null,\"description\":\"Free forever\",\"durationUnit\":null,\"accessLevel\":0,\"isLifetime\":1}",
                MembershipCreateRequest.class
        );

        assertEquals(0, request.price().compareTo(BigDecimal.ZERO));
        assertEquals(1, request.isLifetime());
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsUnknownDurationUnitCode() {
        String json = "{\"name\":\"Plan\",\"price\":9.99,\"duration\":1,\"durationUnit\":400,\"accessLevel\":10,\"isLifetime\":0}";

        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(json, MembershipCreateRequest.class));
    }

    @Test
    void rejectsDurationValuesForLifetimePlan() throws JsonProcessingException {
        MembershipCreateRequest request = objectMapper.readValue(
                "{\"name\":\"Invalid Lifetime\",\"price\":0.00,\"duration\":30,\"durationUnit\":100,\"accessLevel\":0,\"isLifetime\":1}",
                MembershipCreateRequest.class
        );

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsSystemManagedFields() {
        String json = "{\"name\":\"Monthly Plan\",\"price\":9.99,\"duration\":1,\"durationUnit\":200,\"accessLevel\":10,\"isLifetime\":0,\"planId\":\"PLN-999\"}";

        assertThrows(
                JsonProcessingException.class,
                () -> objectMapper.readValue(json, MembershipCreateRequest.class)
        );
    }
}
