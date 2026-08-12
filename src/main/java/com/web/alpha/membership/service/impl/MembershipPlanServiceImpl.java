package com.web.alpha.membership.service.impl;

import com.web.alpha.common.generator.GlobalCodeGenerator;
import com.web.alpha.common.security.CurrentUserProvider;
import com.web.alpha.membership.dto.MembershipCreateRequest;
import com.web.alpha.membership.dto.MembershipPlanResponse;
import com.web.alpha.membership.entity.MembershipPlan;
import com.web.alpha.membership.exception.MembershipPlanNameAlreadyExistsException;
import com.web.alpha.membership.event.MembershipPlanEvent;
import com.web.alpha.membership.event.MembershipPlanEventPublisher;
import com.web.alpha.membership.mapper.MembershipPlanMapper;
import com.web.alpha.membership.repository.MembershipPlanRepository;
import com.web.alpha.membership.service.MembershipPlanService;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private static final Logger log = LoggerFactory.getLogger(MembershipPlanServiceImpl.class);
    private static final int NOT_DELETED = 0;

    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipPlanMapper membershipPlanMapper;
    private final GlobalCodeGenerator globalCodeGenerator;
    private final MembershipPlanEventPublisher eventPublisher;
    private final CurrentUserProvider currentUserProvider;

    public MembershipPlanServiceImpl(
            MembershipPlanRepository membershipPlanRepository,
            MembershipPlanMapper membershipPlanMapper,
            GlobalCodeGenerator globalCodeGenerator,
            MembershipPlanEventPublisher eventPublisher,
            CurrentUserProvider currentUserProvider
    ) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.membershipPlanMapper = membershipPlanMapper;
        this.globalCodeGenerator = globalCodeGenerator;
        this.eventPublisher = eventPublisher;
        this.currentUserProvider = currentUserProvider;
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
        Long currentUserId = currentUserProvider.getUserId();
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
            throw new MembershipPlanNameAlreadyExistsException(exception);
        }

        savedEntity.setPlanId(globalCodeGenerator.generate("PLN", savedEntity.getId()));
        MembershipPlan finalEntity = membershipPlanRepository.save(savedEntity);
        eventPublisher.publishCreated(new MembershipPlanEvent(
                finalEntity.getId(),
                finalEntity.getPlanId(),
                finalEntity.getName(),
                finalEntity.getPrice(),
                finalEntity.getDuration(),
                finalEntity.getDurationUnit(),
                finalEntity.getAccessLevel(),
                finalEntity.getIsLifetime(),
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

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "membership-plan-list-cache" , key = "'membership-plan:all'")
    public List<MembershipPlanResponse> getAll() {
        return   membershipPlanRepository.findAllByIsDeletedOrderByIdDesc(NOT_DELETED).stream()
                .map(membershipPlanMapper::toResponse)
                .toList();
    }

    private void ensureNameIsAvailable(String name) {
        if (membershipPlanRepository.existsByNameIgnoreCaseAndIsDeleted(name, NOT_DELETED)) {
            throw new MembershipPlanNameAlreadyExistsException();
        }
    }

}
