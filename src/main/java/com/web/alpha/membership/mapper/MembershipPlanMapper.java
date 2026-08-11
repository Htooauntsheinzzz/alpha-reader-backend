package com.web.alpha.membership.mapper;

import com.web.alpha.membership.dto.MembershipCreateRequest;
import com.web.alpha.membership.dto.MembershipPlanResponse;
import com.web.alpha.membership.entity.MembershipPlan;
import org.springframework.stereotype.Component;

@Component
public class MembershipPlanMapper {

    public MembershipPlan toEntity(MembershipCreateRequest request) {
        MembershipPlan entity = new MembershipPlan();
        entity.setName(request.name());
        entity.setPrice(request.price());
        entity.setDuration(request.duration());
        entity.setDescription(request.description());
        entity.setDurationUnit(request.durationUnit());
        entity.setAccessLevel(request.accessLevel());
        entity.setIsLifetime(request.isLifetime());
        return entity;
    }

    public MembershipPlanResponse toResponse(MembershipPlan entity) {
        return new MembershipPlanResponse(
                entity.getId(),
                entity.getPlanId(),
                entity.getName(),
                entity.getPrice(),
                entity.getDuration(),
                entity.getDescription(),
                entity.getDurationUnit(),
                entity.getAccessLevel(),
                entity.getIsLifetime(),
                entity.getIsActive(),
                entity.getIsDeleted(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt()
        );
    }
}
