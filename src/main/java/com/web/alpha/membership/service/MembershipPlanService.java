package com.web.alpha.membership.service;

import com.web.alpha.membership.dto.MembershipCreateRequest;
import com.web.alpha.membership.dto.MembershipPlanResponse;

import java.util.List;

public interface MembershipPlanService {

    MembershipPlanResponse create(MembershipCreateRequest request);

    List<MembershipPlanResponse> getAll();


}
