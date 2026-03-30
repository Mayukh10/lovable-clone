package com.MayukhProjects.lovable_clone.controller;

import com.MayukhProjects.lovable_clone.dto.subscription.*;
import com.MayukhProjects.lovable_clone.service.PaymentProcessor;
import com.MayukhProjects.lovable_clone.service.PlanService;
import com.MayukhProjects.lovable_clone.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
//@RequestMapping("/api/auth")
public class BillingController {

    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    private final PaymentProcessor paymentProcessor;


    @Value("${webhook.secret}")
    private String secretwebhook;

    @GetMapping("/api/plans")
    public ResponseEntity<List<PlanResponse>> getAllplans(){

        return ResponseEntity.ok(planService.getAllplans());
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(){
        return ResponseEntity.ok(subscriptionService.getMySubscription());
    }

    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> CreateCheckoutResponse(
            @RequestBody CheckoutRequest request) {

        return ResponseEntity.ok(paymentProcessor.CreateCheckoutResponse(request));
    }

    @PostMapping("/api/payments/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal() {

        return ResponseEntity.ok(paymentProcessor.openCustomerPortal());
    }

    @PostMapping("/webhook/payment")
    public ResponseEntity<String> handlePaymentWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        Event event;
        log.info("🔥 Stripe webhook received");

        try {
            event = Webhook.constructEvent(payload, sigHeader, secretwebhook);

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if (deserializer.getObject().isPresent()) {
                stripeObject = deserializer.getObject().get();
            } else {
                try {
                    stripeObject = deserializer.deserializeUnsafe();

                    if (stripeObject == null) {
                        log.warn("Failed to deserialize webhook object for event: {}", event.getType());
                        return ResponseEntity.ok().build();
                    }

                } catch (Exception e) {
                    log.error(
                            "Unsafe deserialization failed for event {}: {}",
                            event.getType(),
                            e.getMessage()
                    );
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Deserialization failed");
                }
            }

            Map<String, String> metadata = new HashMap<>();

            if (stripeObject instanceof Session session) {
                metadata = session.getMetadata();
            }

            paymentProcessor.handleWebhookEvent(
                    event.getType(),
                    stripeObject,
                    metadata
            );

            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe Signature",e);
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid Signature");
        }
    }






}
