package com.MayukhProjects.lovable_clone.controller;


import com.MayukhProjects.lovable_clone.dto.subscription.CheckoutRequest;
import com.MayukhProjects.lovable_clone.dto.subscription.UsageTodayResponse;
import com.MayukhProjects.lovable_clone.service.UsageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/usage")
public class UsageController {


    private final UsageService usageService;

    @GetMapping("/today")
    public ResponseEntity<UsageTodayResponse> getTodayUsage(){
        Long UserId = 1L;
        return ResponseEntity.ok(usageService.getTodayUsage());
    }

    @GetMapping("/limits")
    public ResponseEntity<CheckoutRequest.PlanLimitsResponse> getPlanLimits (){
        Long UserId = 1L;
        return ResponseEntity.ok(usageService.getPlanLimits());
    }



}
