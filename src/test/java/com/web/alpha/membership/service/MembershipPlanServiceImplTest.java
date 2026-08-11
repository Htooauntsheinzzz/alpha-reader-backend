package com.web.alpha.membership.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.web.alpha.common.generator.GlobalCodeGenerator;
import com.web.alpha.common.security.CurrentUserProvider;
import com.web.alpha.membership.dto.MembershipCreateRequest;
import com.web.alpha.membership.dto.MembershipPlanResponse;
import com.web.alpha.membership.entity.MembershipPlan;
import com.web.alpha.membership.enums.MembershipDurationUnit;
import com.web.alpha.membership.event.MembershipPlanEvent;
import com.web.alpha.membership.event.MembershipPlanEventPublisher;
import com.web.alpha.membership.mapper.MembershipPlanMapper;
import com.web.alpha.membership.repository.MembershipPlanRepository;
import com.web.alpha.membership.service.impl.MembershipPlanServiceImpl;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class MembershipPlanServiceImplTest {

    private MembershipPlanRepository repository;
    private MembershipPlanEventPublisher eventPublisher;
    private MembershipPlanService service;

    @BeforeEach
    void setUp() {
        repository = mock(MembershipPlanRepository.class);
        eventPublisher = mock(MembershipPlanEventPublisher.class);
        service = new MembershipPlanServiceImpl(
                repository,
                new MembershipPlanMapper(),
                new GlobalCodeGenerator(),
                eventPublisher,
                new CurrentUserProvider()
        );
        Jwt jwt = Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .subject("7")
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void flushesNullPlanIdForDatabaseIdThenSavesGeneratedPlanId() {
        AtomicReference<String> firstFlushedPlanId = new AtomicReference<>();
        AtomicReference<String> finalSavedPlanId = new AtomicReference<>();
        when(repository.saveAndFlush(any(MembershipPlan.class))).thenAnswer(invocation -> {
            MembershipPlan entity = invocation.getArgument(0);
            firstFlushedPlanId.set(entity.getPlanId());
            entity.setId(12L);
            return entity;
        });
        when(repository.save(any(MembershipPlan.class))).thenAnswer(invocation -> {
            MembershipPlan entity = invocation.getArgument(0);
            finalSavedPlanId.set(entity.getPlanId());
            return entity;
        });

        MembershipPlanResponse response = service.create(new MembershipCreateRequest(
                "Monthly Plan",
                new BigDecimal("9.99"),
                1L,
                "Monthly membership",
                MembershipDurationUnit.MONTH,
                10,
                0
        ));

        assertNull(firstFlushedPlanId.get());
        assertEquals("PLN-012", finalSavedPlanId.get());
        assertEquals("PLN-012", response.planId());
        assertEquals(7L, response.createdBy());
        assertEquals(7L, response.updateBy());
        assertEquals(1, response.isActive());
        assertEquals(0, response.isDeleted());
        assertEquals(MembershipDurationUnit.MONTH, response.durationUnit());
        assertEquals(10, response.accessLevel());
        assertEquals(0, response.isLifetime());
        assertNotNull(response.createdAt());
        assertEquals(response.createdAt(), response.updateAt());

        ArgumentCaptor<MembershipPlanEvent> eventCaptor =
                ArgumentCaptor.forClass(MembershipPlanEvent.class);
        verify(eventPublisher).publishCreated(eventCaptor.capture());
        assertEquals("PLN-012", eventCaptor.getValue().planId());
        assertEquals(7L, eventCaptor.getValue().performedBy());
        assertEquals(MembershipDurationUnit.MONTH, eventCaptor.getValue().durationUnit());
    }
}
