package com.MayukhProjects.lovable_clone.mapper;

import com.MayukhProjects.lovable_clone.dto.member.MemberResponse;
import com.MayukhProjects.lovable_clone.entity.ProjectMember;
import com.MayukhProjects.lovable_clone.entity.User;
import com.MayukhProjects.lovable_clone.enums.ProjectRole;
import java.time.Instant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-18T22:09:23+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Azul Systems, Inc.)"
)
@Component
public class ProjectMemberMapperImpl implements ProjectMemberMapper {

    @Override
    public MemberResponse toProjectMemberResponseFromOwner(User owner) {
        if ( owner == null ) {
            return null;
        }

        Long userId = null;
        String username = null;
        String name = null;

        userId = owner.getId();
        username = owner.getUsername();
        name = owner.getName();

        ProjectRole projectRole = ProjectRole.OWNER;
        Instant invitedAt = null;

        MemberResponse memberResponse = new MemberResponse( userId, username, name, projectRole, invitedAt );

        return memberResponse;
    }

    @Override
    public MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember) {
        if ( projectMember == null ) {
            return null;
        }

        Long userId = null;
        String username = null;
        String name = null;
        ProjectRole projectRole = null;
        Instant invitedAt = null;

        userId = projectMemberUserId( projectMember );
        username = projectMemberUserUsername( projectMember );
        name = projectMemberUserName( projectMember );
        projectRole = projectMember.getProjectRole();
        invitedAt = projectMember.getInvitedAt();

        MemberResponse memberResponse = new MemberResponse( userId, username, name, projectRole, invitedAt );

        return memberResponse;
    }

    private Long projectMemberUserId(ProjectMember projectMember) {
        User user = projectMember.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private String projectMemberUserUsername(ProjectMember projectMember) {
        User user = projectMember.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getUsername();
    }

    private String projectMemberUserName(ProjectMember projectMember) {
        User user = projectMember.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getName();
    }
}
