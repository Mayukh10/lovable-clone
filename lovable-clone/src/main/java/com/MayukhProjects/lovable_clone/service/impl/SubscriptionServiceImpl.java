package com.MayukhProjects.lovable_clone.service.impl;

import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutRequest;
import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutResponse;
import com.MayukhProjects.lovable_clone.dto.subscription.PortalResponse;
import com.MayukhProjects.lovable_clone.dto.subscription.SubscriptionResponse;
import com.MayukhProjects.lovable_clone.entity.Plan;
import com.MayukhProjects.lovable_clone.entity.Subscription;
import com.MayukhProjects.lovable_clone.entity.User;
import com.MayukhProjects.lovable_clone.enums.SubscriptionStatus;
import com.MayukhProjects.lovable_clone.error.ResourceNotFoundException;
import com.MayukhProjects.lovable_clone.mapper.SubscriptionMapper;
import com.MayukhProjects.lovable_clone.repository.PlanRepository;
import com.MayukhProjects.lovable_clone.repository.ProjectMemberRepository;
import com.MayukhProjects.lovable_clone.repository.SubscriptionRepository;
import com.MayukhProjects.lovable_clone.repository.UserRepository;
import com.MayukhProjects.lovable_clone.security.AuthUtil;
import com.MayukhProjects.lovable_clone.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final ProjectMemberRepository projectMemberRepository;
   // private final SubscriptionStatus subscriptionStatus;

    @Override
    public SubscriptionResponse getMySubscription() {

        Long userId = authUtil.getCurrentUserId();
        var currentSubscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE,SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRIALING
        )).orElse (new Subscription());

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);

    }

    @Override
    public void activeSubscription(Long userId, Long planId, String subscriptionId, String customerId) {

        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if(exists) return;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user", userId.toString()));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("plan", planId.toString()));


        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .currentPeriodStart(Instant.now())
                .build();

        subscriptionRepository.save(subscription);

    }


    

    @Transactional
    @Override
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {

        Subscription subscription = getSubscription(gatewaySubscriptionId);

        boolean subscriptionHasBeenUpdated = false;

        if(status != null && status != subscription.getStatus()){
            subscription.setStatus(status);
            subscriptionHasBeenUpdated = true;

        }
        if(periodStart != null && !periodStart.equals(subscription.getCurrentPeriodStart())){
            subscription.setCurrentPeriodStart(periodStart);
        }

        if(periodEnd != null && !periodEnd.equals(subscription.getCancelAtPeriodEnd())){
            subscription.setCurrentPeriodEnd(periodEnd);

        }
        if(cancelAtPeriodEnd != null && cancelAtPeriodEnd != subscription.getCancelAtPeriodEnd()){
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
        }

        if(planId != null && !planId.equals(subscription.getPlan().getId())){
            Plan plan  = planRepository.findById(planId).orElseThrow(() -> new ResourceNotFoundException("Plan", planId.toString()));
            subscription.setPlan(plan);


        }

        if(subscriptionHasBeenUpdated){
            log.debug("Subscription has been Updated: {}",gatewaySubscriptionId);
            subscriptionRepository.save(subscription);

        }

    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);



    }

    @Override
    public void markSubscriptionPastDue(String gatewaySubscriptionId) {

        Subscription subscription = getSubscription(gatewaySubscriptionId);

        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE){
            log.debug("Subscripton is already past Due,gatewaySubscriptionId: {}",gatewaySubscriptionId);
            return;
        }

        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);



    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {

        Subscription subscription = subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId).orElseThrow(() ->
                new ResourceNotFoundException("subscription", gatewaySubscriptionId));

        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();

        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE){
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);


    }

    private final Integer FREE_TIER_PROJECTS_ALLOWED = 100;

    @Override
    public boolean canCreateNewProject() {
        Long userId = authUtil.getCurrentUserId();
        SubscriptionResponse currentSubscription = getMySubscription();

        int countOfOwnedProjects = projectMemberRepository.countProjectOwnedByUser(userId);

        if(currentSubscription.plan() == null){
             return countOfOwnedProjects < FREE_TIER_PROJECTS_ALLOWED;
        }

        return countOfOwnedProjects < currentSubscription.plan().maxProjects();
    }

    private Subscription getSubscription(String gatewaySubscriptionId){
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription",gatewaySubscriptionId));

    }
}
