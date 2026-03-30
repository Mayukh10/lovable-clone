package com.MayukhProjects.lovable_clone.service;

import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutRequest;
import com.MayukhProjects.lovable_clone.dto.subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {


     CheckoutRequest.PlanLimitsResponse getPlanLimits();

     UsageTodayResponse getTodayUsage();
}
