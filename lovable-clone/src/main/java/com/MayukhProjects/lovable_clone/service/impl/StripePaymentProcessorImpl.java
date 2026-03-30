package com.MayukhProjects.lovable_clone.service.impl;

import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutRequest;
import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutResponse;
import com.MayukhProjects.lovable_clone.dto.subscription.PortalResponse;
import com.MayukhProjects.lovable_clone.entity.Plan;
import com.MayukhProjects.lovable_clone.entity.User;
import com.MayukhProjects.lovable_clone.enums.SubscriptionStatus;
import com.MayukhProjects.lovable_clone.error.BadRequestException;
import com.MayukhProjects.lovable_clone.error.ResourceNotFoundException;
import com.MayukhProjects.lovable_clone.repository.PlanRepository;
import com.MayukhProjects.lovable_clone.repository.UserRepository;
import com.MayukhProjects.lovable_clone.security.AuthUtil;
import com.MayukhProjects.lovable_clone.service.PaymentProcessor;
import com.MayukhProjects.lovable_clone.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentProcessorImpl implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    @Value("${client.url}")
    private String frontendUrl;

    @Override
    public CheckoutResponse CreateCheckoutResponse(CheckoutRequest request) {

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(()->new ResourceNotFoundException("Plan",request.planId().toString()));

        Long userId = authUtil.getCurrentUserId();

        User user = getUser(userId);

        var params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPrice(plan.getStripePriceId()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        SessionCreateParams.SubscriptionData.builder()
                                .setBillingMode(
                                        SessionCreateParams.SubscriptionData.BillingMode.builder()
                                                .setType(
                                                        SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE
                                                )
                                                .build()
                                )
                                .build()
                )

                .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel.html")
                .putMetadata("user_id",userId.toString())
                .putMetadata("plan_id", plan.getId().toString());

        try {

            String stripeCustomerId = user.getStripeCustomerId();

            if(stripeCustomerId == null || stripeCustomerId.isEmpty()){
                params.setCustomerEmail(user.getUsername());
            }else {
                params.setCustomer(stripeCustomerId);

            }
            Session session = Session.create(params.build());
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public PortalResponse openCustomerPortal() {

        Long userId = authUtil.getCurrentUserId();
        User user = getUser(userId);

        String stripeCustomerId = user.getStripeCustomerId();

        try {
            // ✅ Create Stripe customer if missing
            if (stripeCustomerId == null || stripeCustomerId.isBlank()) {

                Customer customer = Customer.create(
                        Map.of(
                                "email", user.getUsername(), // or user.getEmail()
                                "name", user.getName(),
                                "metadata", Map.of("user_id", userId.toString())
                        )
                );

                stripeCustomerId = customer.getId();
                user.setStripeCustomerId(stripeCustomerId);
                userRepository.save(user);
            }

            var portalSession =
                    com.stripe.model.billingportal.Session.create(
                            com.stripe.param.billingportal.SessionCreateParams.builder()
                                    .setCustomer(stripeCustomerId)
                                    .setReturnUrl(frontendUrl)
                                    .build()
                    );

            return new PortalResponse(portalSession.getUrl());

        } catch (StripeException e) {
            log.error("Stripe portal error for user {}", userId, e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
        log.debug("Handling Stripe Events: {}", type);

        switch (type){
            case "checkout.session.completed" -> handleCheckoutSessionCompleted((Session) stripeObject);// one time,on checkout completed
            case "customer.subscription.created" -> handleCustomerSubscriptionUpdated((Subscription) stripeObject);
            case "customer.subscription.updated" -> handleCustomerSubscriptionUpdated((Subscription) stripeObject); //when user cancels,upgrades or any updates
            case "customer.subscription.deleted" -> handleCustomerSubscriptionDeleted((Subscription) stripeObject); // when subscription ends
            case "invoice.paid" -> handleInvoicePaid((Invoice) stripeObject); // when invoice is paid
            case "invoice.payment_failed" -> handleInvoicePaymentFailed((Invoice) stripeObject ); // when invoice is not paid, mark as past due
            default -> log.debug("Ignoring the Event: {}",type);
        }



    }

    private void handleCheckoutSessionCompleted(Session session){
        Map<String,String> metadata = session.getMetadata();
        log.error("🧾 Checkout metadata = {}", metadata);
        if (metadata == null || metadata.isEmpty()) {
            log.error("Stripe session metadata missing for session {}", session.getId());
            return;
        }

        Long userId = Long.parseLong(metadata.get("user_id"));
        Long planId = Long.parseLong(metadata.get("plan_id"));

        String subscriptionId = session.getSubscription();
        String customerId = session.getCustomer();

        User user = getUser(userId);

        if(user.getStripeCustomerId() == null){
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }

        subscriptionService.activeSubscription(userId,planId,subscriptionId,customerId);


    }

    private void handleCustomerSubscriptionCreated(Subscription subscription) {

        log.info("🆕 Subscription created in Stripe: {}", subscription.getId());

        String customerId = subscription.getCustomer();
        SubscriptionItem item = subscription.getItems().getData().get(0);

        Long planId = resolvePlanId(item.getPrice());
        if (planId == null) {
            log.error("Plan not found for price {}", item.getPrice().getId());
            return;
        }

        User user = userRepository.findByStripeCustomerId(customerId)
                .orElseThrow(() ->
                        new RuntimeException("User not found for Stripe customer " + customerId)
                );

        Instant periodStart = toInstant(item.getCurrentPeriodStart());
        Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

        subscriptionService.activeSubscription(
                user.getId(),
                planId,
                subscription.getId(),
                customerId
        );

        log.info("✅ Subscription saved in DB for user {}", user.getId());
    }

    private void handleInvoicePaid(Invoice invoice){

        String subId = extractSubscriptionId(invoice);
        if(subId == null) return;

        try{
            Subscription subscription = Subscription.retrieve(subId);
            var item = subscription.getItems().getData().get(0);

            Instant periodStart = toInstant(item.getCurrentPeriodStart());
            Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

            subscriptionService.renewSubscriptionPeriod(
                    subId,
                    periodStart,
                    periodEnd
                );
        }catch (StripeException e){
            throw new RuntimeException(e);
        }


    }
    private void handleCustomerSubscriptionUpdated(Subscription subscription){

        if(subscription == null){
            log.error("subscription object was null");
            return;
        }

        SubscriptionStatus status = mapStripeStatusToEnum(subscription.getStatus());

        if(status == null){
            log.warn("Unknown status '{}' for subscription{}",subscription.getStatus(), subscription.getId());
            return;
        }

        SubscriptionItem item = subscription.getItems().getData().get(0);
        Instant periodStart = toInstant(item.getCurrentPeriodStart());
        Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

        Long planId = resolvePlanId(item.getPrice());

        subscriptionService.updateSubscription(
                subscription.getId(),status,periodStart,periodEnd,
                subscription.getCancelAtPeriodEnd(),planId
        );
    }
    private void handleCustomerSubscriptionDeleted(Subscription subscription){

        if(subscription == null){
            log.error("subscription object was null");
            return;
        }

        subscriptionService.cancelSubscription(subscription.getId());



    }
    private void handleInvoicePaymentFailed(Invoice invoice){

        String subId = extractSubscriptionId(invoice);
        if(subId == null) return;

        subscriptionService.markSubscriptionPastDue(subId);

    }
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("user", userId.toString()));
    }

    private SubscriptionStatus mapStripeStatusToEnum(String status){
        return switch (status){
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trailing" -> SubscriptionStatus.TRIALING;
            case "past_due", "unpaid","paused","incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELED;
            case "incomplete" ->SubscriptionStatus.INCOMPLETE;
            default -> {
                log.warn("Unmapped Stripe status: {}", status);
                yield null;
            }
        };
    }

    private Instant toInstant(Long epoch){
        return epoch != null ? Instant.ofEpochSecond(epoch) : null;
    }

    private Long resolvePlanId(Price price){
        if(price == null || price.getId() == null) return null;
        return planRepository.findByStripePriceId(price.getId())
                .map(Plan::getId)
                .orElse(null);
     }

     private String extractSubscriptionId(Invoice invoice){
        var parent = invoice.getParent();
        if(parent == null) return null;

        var subDetails = parent.getSubscriptionDetails();
        if(subDetails == null) return null;

        return subDetails.getSubscription();
     }

}
