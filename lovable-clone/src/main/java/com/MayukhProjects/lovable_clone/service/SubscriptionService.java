package com.MayukhProjects.lovable_clone.service;

import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutRequest;
import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutResponse;
import com.MayukhProjects.lovable_clone.dto.subscription.PortalResponse;
import com.MayukhProjects.lovable_clone.dto.subscription.SubscriptionResponse;
import com.MayukhProjects.lovable_clone.enums.SubscriptionStatus;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface SubscriptionService {



    SubscriptionResponse getMySubscription();

    void activeSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String gatewaySubscriptionId);

    void markSubscriptionPastDue(String gatewaySubscriptionId);

    void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd);

    boolean canCreateNewProject();
}
