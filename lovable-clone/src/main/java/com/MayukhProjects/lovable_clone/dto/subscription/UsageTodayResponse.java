package com.MayukhProjects.lovable_clone.dto.subscription;

public record UsageTodayResponse(

        Integer tokensUsed,
        Integer tokenLimit,
        Integer previewRunning,
        Integer previewsLimit


) {
}
