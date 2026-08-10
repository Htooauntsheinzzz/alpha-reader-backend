package com.web.alpha.membership.service.impl;

import com.web.alpha.common.generator.GlobalCodeGenerator;
import com.web.alpha.membership.dto.MembershipCreateRequest;
import com.web.alpha.membership.dto.MembershipPlanResponse;
import com.web.alpha.membership.entity.MembershipPlan;
import com.web.alpha.membership.event.MembershipPlanEvent;
import com.web.alpha.membership.event.MembershipPlanEventPublisher;
import com.web.alpha.membership.mapper.MembershipPlanMapper;
import com.web.alpha.membership.repository.MembershipPlanRepository;
import com.web.alpha.membership.service.MembershipPlanService;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private static final Logger log = LoggerFactory.getLogger(MembershipPlanServiceImpl.class);
    private static final int NOT_DELETED = 0;

    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipPlanMapper membershipPlanMapper;
    private final GlobalCodeGenerator globalCodeGenerator;
    private final MembershipPlanEventPublisher eventPublisher;

    public MembershipPlanServiceImpl(
            MembershipPlanRepository membershipPlanRepository,
            MembershipPlanMapper membershipPlanMapper,
            GlobalCodeGenerator globalCodeGenerator,
            MembershipPlanEventPublisher eventPublisher
    ) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.membershipPlanMapper = membershipPlanMapper;
        this.globalCodeGenerator = globalCodeGenerator;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(
                    cacheNames = "membership-plan-cache",
                    key = "'membership-plan:' + #result.id()"
            ),
            evict = @CacheEvict(
                    cacheNames = "membership-plan-list-cache",
                    key = "'membership-plan:all'"
            )
    )
    public MembershipPlanResponse create(MembershipCreateRequest request) {
        Long currentUserId = getCurrentUserId();
        log.info("Creating membership plan actorUserId={} name={}", currentUserId, request.name());
        ensureNameIsAvailable(request.name());

        LocalDateTime now = LocalDateTime.now();
        MembershipPlan entity = membershipPlanMapper.toEntity(request);
        entity.setIsActive(1);
        entity.setIsDeleted(0);
        entity.setCreatedBy(currentUserId);
        entity.setCreatedAt(now);
        entity.setUpdatedBy(currentUserId);
        entity.setUpdatedAt(now);

        MembershipPlan savedEntity;
        try {
            savedEntity = membershipPlanRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Membership plan name already exists",
                    exception
            );
        }

        savedEntity.setPlanId(globalCodeGenerator.generate("PLN", savedEntity.getId()));
        MembershipPlan finalEntity = membershipPlanRepository.save(savedEntity);
        eventPublisher.publishCreated(new MembershipPlanEvent(
                finalEntity.getId(),
                finalEntity.getPlanId(),
                finalEntity.getName(),
                finalEntity.getPrice(),
                finalEntity.getDuration(),
                currentUserId,
                LocalDateTime.now()
        ));
        log.info(
                "Membership plan created membershipPlanId={} planId={} actorUserId={}",
                finalEntity.getId(),
                finalEntity.getPlanId(),
                currentUserId
        );
        return membershipPlanMapper.toResponse(finalEntity);
    }

    private void ensureNameIsAvailable(String name) {
        if (membershipPlanRepository.existsByNameIgnoreCaseAndIsDeleted(name, NOT_DELETED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Membership plan name already exists");
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException | NullPointerException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT subject must be a user id");
        }
    }
}
