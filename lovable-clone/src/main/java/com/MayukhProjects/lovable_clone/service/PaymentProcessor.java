package com.MayukhProjects.lovable_clone.service;

import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutRequest;
import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutResponse;
import com.MayukhProjects.lovable_clone.dto.subscription.PortalResponse;
import com.stripe.model.StripeObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;


public interface PaymentProcessor {
    CheckoutResponse CreateCheckoutResponse(CheckoutRequest request);

    PortalResponse openCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
