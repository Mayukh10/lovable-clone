package com.MayukhProjects.lovable_clone.dto.subscription;

public record CheckoutRequest(
        Long planId
) {
    public static record PlanLimitsResponse(String planName,
                                            int maxTokensPerday,
                                            int maxProjects,
                                            boolean UnlimitedAi) {


    }
}
