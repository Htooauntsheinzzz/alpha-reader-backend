package com.web.alpha.membership.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MembershipCreateRequestDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void deserializesValidBusinessFields() throws JsonProcessingException {
        MembershipCreateRequest request = objectMapper.readValue(
                "{\"name\":\"Monthly Plan\",\"price\":9.99,\"duration\":30,\"description\":\"Monthly membership\"}",
                MembershipCreateRequest.class
        );

        assertEquals("Monthly Plan", request.name());
        assertEquals(new BigDecimal("9.99"), request.price());
        assertEquals(30L, request.duration());
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsSystemManagedFields() {
        String json = "{\"name\":\"Monthly Plan\",\"price\":9.99,\"duration\":30,\"planId\":\"PLN-999\"}";

        assertThrows(
                JsonProcessingException.class,
                () -> objectMapper.readValue(json, MembershipCreateRequest.class)
        );
    }
}
