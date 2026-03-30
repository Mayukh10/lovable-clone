package com.MayukhProjects.lovable_clone.service.impl;

import com.MayukhProjects.lovable_clone.dto.member.InviteMemberRequest;
import com.MayukhProjects.lovable_clone.dto.member.MemberResponse;
import com.MayukhProjects.lovable_clone.dto.member.UpdateMemberRoleRequest;
import com.MayukhProjects.lovable_clone.entity.Project;
import com.MayukhProjects.lovable_clone.entity.ProjectMember;
import com.MayukhProjects.lovable_clone.entity.ProjectMemberId;
import com.MayukhProjects.lovable_clone.entity.User;
import com.MayukhProjects.lovable_clone.error.ResourceNotFoundException;
import com.MayukhProjects.lovable_clone.mapper.ProjectMemberMapper;
import com.MayukhProjects.lovable_clone.repository.ProjectMemberRepository;
import com.MayukhProjects.lovable_clone.repository.ProjectRepository;
import com.MayukhProjects.lovable_clone.repository.UserRepository;
import com.MayukhProjects.lovable_clone.security.AuthUtil;
import com.MayukhProjects.lovable_clone.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMember(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        Project project = getAccessibleProjectById(projectId, userId);

        return projectMemberRepository.findByIdProjectId(projectId)
                        .stream()
                        .map(projectMemberMapper::toProjectMemberResponseFromMember)
                        .toList();

    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);



        User invitee = userRepository.findByUsername(request.username()).orElseThrow(()-> new ResourceNotFoundException("User",request.username()));

        if (invitee.getId().equals(userId)) {
            throw new RuntimeException("Cannot invite yourself");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());

        if (projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Cannot Invite once again");
        }

        ProjectMember member = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitee)
                .projectRole(request.role())
                .build();

        projectMemberRepository.save(member);

        return projectMemberMapper.toProjectMemberResponseFromMember(member);
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);



        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow(()->new ResourceNotFoundException("ProjectMember",
                "projectId=" + projectId + ", memberId=" + memberId));

        projectMember.setProjectRole(request.role());

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);


    }


    @Override
    public void deleteMemberRole(Long projectId, Long memberId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);



        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if (projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Cannot invite Once again");

        }
        projectMemberRepository.deleteById(projectMemberId);
    }


    //internal fucntion
//    private Project getAccessibleProjectById(Long projectId, Long userId) {
//        return projectRepository.findAccessibleProjectId(projectId, userId)
//                .orElseThrow(()-> new ResourceNotFoundException("Project",projectId.toString()));
//    }


    private Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectId(projectId, userId)
                .orElseThrow(()-> new ResourceNotFoundException("Project",projectId.toString()));
    }

}





