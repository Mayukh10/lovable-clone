package com.MayukhProjects.lovable_clone.dto.member;

import com.MayukhProjects.lovable_clone.enums.ProjectRole;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String username,
        String name,
        ProjectRole projectRole,
        Instant invitedAt

){
}
