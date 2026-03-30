package com.MayukhProjects.lovable_clone.dto.project;

import com.MayukhProjects.lovable_clone.dto.auth.UserProfileResponse;
import com.MayukhProjects.lovable_clone.entity.User;

import java.time.Instant;



public record ProjectResponse(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt




) {
}
