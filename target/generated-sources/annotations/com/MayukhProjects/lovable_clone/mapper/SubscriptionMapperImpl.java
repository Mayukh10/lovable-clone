package com.MayukhProjects.lovable_clone.mapper;

import com.MayukhProjects.lovable_clone.dto.subscription.PlanResponse;
import com.MayukhProjects.lovable_clone.dto.subscription.SubscriptionResponse;
import com.MayukhProjects.lovable_clone.entity.Plan;
import com.MayukhProjects.lovable_clone.entity.Subscription;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-18T22:09:24+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Azul Systems, Inc.)"
)
@Component
public class SubscriptionMapperImpl implements SubscriptionMapper {

    @Override
    public SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
        if ( subscription == null ) {
            return null;
        }

        PlanResponse plan = null;
        String status = null;

        plan = toPlanResponse( subscription.getPlan() );
        if ( subscription.getStatus() != null ) {
            status = subscription.getStatus().name();
        }

        Instant periodEnd = null;
        Long tokenUsedThisCycle = null;

        SubscriptionResponse subscriptionResponse = new SubscriptionResponse( plan, status, periodEnd, tokenUsedThisCycle );

        return subscriptionResponse;
    }

    @Override
    public PlanResponse toPlanResponse(Plan plan) {
        if ( plan == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        Integer maxProjects = null;
        Integer maxTokensPerDay = null;
        Boolean unlimitedAi = null;

        id = plan.getId();
        name = plan.getName();
        maxProjects = plan.getMaxProjects();
        maxTokensPerDay = plan.getMaxTokensPerDay();
        unlimitedAi = plan.getUnlimitedAi();

        String price = null;

        PlanResponse planResponse = new PlanResponse( id, name, maxProjects, maxTokensPerDay, unlimitedAi, price );

        return planResponse;
    }
}
