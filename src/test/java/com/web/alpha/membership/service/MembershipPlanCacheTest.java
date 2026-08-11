package com.web.alpha.membership.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.web.alpha.common.generator.GlobalCodeGenerator;
import com.web.alpha.common.security.CurrentUserProvider;
import com.web.alpha.membership.dto.MembershipCreateRequest;
import com.web.alpha.membership.dto.MembershipPlanResponse;
import com.web.alpha.membership.entity.MembershipPlan;
import com.web.alpha.membership.enums.MembershipDurationUnit;
import com.web.alpha.membership.event.MembershipPlanEventPublisher;
import com.web.alpha.membership.mapper.MembershipPlanMapper;
import com.web.alpha.membership.repository.MembershipPlanRepository;
import com.web.alpha.membership.service.impl.MembershipPlanServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MembershipPlanCacheTest.CacheTestConfig.class)
class MembershipPlanCacheTest {

    @Autowired
    private MembershipPlanService service;

    @Autowired
    private MembershipPlanRepository repository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        reset(repository);
        cache("membership-plan-cache").clear();
        cache("membership-plan-list-cache").clear();
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
    void createCachesResponseAndEvictsList() {
        cache("membership-plan-list-cache").put("membership-plan:all", List.of("stale"));
        when(repository.saveAndFlush(any(MembershipPlan.class))).thenAnswer(invocation -> {
            MembershipPlan entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        when(repository.save(any(MembershipPlan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MembershipPlanResponse response = service.create(new MembershipCreateRequest(
                "Monthly Plan",
                new BigDecimal("9.99"),
                1L,
                "Monthly membership",
                MembershipDurationUnit.MONTH,
                10,
                0
        ));

        assertEquals(
                response,
                cache("membership-plan-cache").get("membership-plan:1", MembershipPlanResponse.class)
        );
        assertNull(cache("membership-plan-list-cache").get("membership-plan:all"));
    }

    private Cache cache(String name) {
        Cache cache = cacheManager.getCache(name);
        assertNotNull(cache);
        return cache;
    }

    @Configuration
    @EnableCaching
    static class CacheTestConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("membership-plan-cache", "membership-plan-list-cache");
        }

        @Bean
        MembershipPlanRepository membershipPlanRepository() {
            return mock(MembershipPlanRepository.class);
        }

        @Bean
        MembershipPlanEventPublisher membershipPlanEventPublisher() {
            return mock(MembershipPlanEventPublisher.class);
        }

        @Bean
        MembershipPlanService membershipPlanService(
                MembershipPlanRepository repository,
                MembershipPlanEventPublisher eventPublisher
        ) {
            return new MembershipPlanServiceImpl(
                    repository,
                    new MembershipPlanMapper(),
                    new GlobalCodeGenerator(),
                    eventPublisher,
                    new CurrentUserProvider()
            );
        }
    }
}
