package com.MayukhProjects.lovable_clone.service;

import com.MayukhProjects.lovable_clone.dto.subscription.PlanResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface PlanService {
     List<PlanResponse> getAllplans();
}
