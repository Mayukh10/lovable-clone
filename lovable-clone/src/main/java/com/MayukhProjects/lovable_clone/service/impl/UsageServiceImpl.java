package com.MayukhProjects.lovable_clone.service.impl;

import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutRequest;
import com.MayukhProjects.lovable_clone.dto.subscription.UsageTodayResponse;
import com.MayukhProjects.lovable_clone.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public CheckoutRequest.PlanLimitsResponse getPlanLimits() {
        return null;
    }

    @Override
    public UsageTodayResponse getTodayUsage() {
        return null;
    }
}
