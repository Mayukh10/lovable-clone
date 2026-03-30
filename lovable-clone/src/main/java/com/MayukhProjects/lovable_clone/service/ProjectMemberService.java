package com.MayukhProjects.lovable_clone.service;

import com.MayukhProjects.lovable_clone.dto.member.InviteMemberRequest;
import com.MayukhProjects.lovable_clone.dto.member.MemberResponse;
import com.MayukhProjects.lovable_clone.dto.member.UpdateMemberRoleRequest;
import com.MayukhProjects.lovable_clone.entity.Project;

import java.util.List;

public interface ProjectMemberService {

    List<MemberResponse> getProjectMember(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

     MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

     void deleteMemberRole(Long projectId, Long memberId);
}