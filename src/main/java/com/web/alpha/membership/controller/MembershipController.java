package com.web.alpha.membership.controller;

import com.web.alpha.membership.dto.MembershipCreateRequest;
import com.web.alpha.membership.dto.MembershipPlanResponse;
import com.web.alpha.membership.service.MembershipPlanService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/membership-plans")
public class MembershipController {

    private final MembershipPlanService membershipPlanService;

    public MembershipController(MembershipPlanService membershipPlanService) {
        this.membershipPlanService = membershipPlanService;
    }

    @PostMapping
    public ResponseEntity<MembershipPlanResponse> create(
            @Valid @RequestBody MembershipCreateRequest request
    ) {
        MembershipPlanResponse response = membershipPlanService.create(request);
        return ResponseEntity.created(
                URI.create("/api/v1/admin/membership-plans/" + response.id())
        ).body(response);
    }



}
