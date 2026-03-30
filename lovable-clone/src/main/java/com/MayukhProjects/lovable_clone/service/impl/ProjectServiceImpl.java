package com.MayukhProjects.lovable_clone.service.impl;

import com.MayukhProjects.lovable_clone.dto.project.ProjectRequest;
import com.MayukhProjects.lovable_clone.dto.project.ProjectResponse;
import com.MayukhProjects.lovable_clone.dto.project.ProjectSummaryResponse;
import com.MayukhProjects.lovable_clone.entity.Project;
import com.MayukhProjects.lovable_clone.entity.ProjectMember;
import com.MayukhProjects.lovable_clone.entity.ProjectMemberId;
import com.MayukhProjects.lovable_clone.entity.User;
import com.MayukhProjects.lovable_clone.enums.ProjectRole;
import com.MayukhProjects.lovable_clone.error.BadRequestException;
import com.MayukhProjects.lovable_clone.mapper.ProjectMapper;
import com.MayukhProjects.lovable_clone.repository.ProjectMemberRepository;
import com.MayukhProjects.lovable_clone.repository.ProjectRepository;
import com.MayukhProjects.lovable_clone.repository.UserRepository;
import com.MayukhProjects.lovable_clone.security.AuthUtil;
import com.MayukhProjects.lovable_clone.service.ProjectService;
import com.MayukhProjects.lovable_clone.service.ProjectTemplateService;
import com.MayukhProjects.lovable_clone.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;
    private final SubscriptionService subscriptionService;
    private final ProjectTemplateService projectTemplateService;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {

        if(!subscriptionService.canCreateNewProject()){
            throw new BadRequestException("USer cannot create a New project with current Plan, Upgrade plan Now.");
        }
        Long userId = authUtil.getCurrentUserId();
       // User owner = userRepository.findById(userId).orElseThrow();
      User owner = userRepository.getReferenceById(userId);


        Project project = Project.builder()
                .name(request.name())
                .build();
        project = projectRepository.save(project);
        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(),owner.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();

        projectMemberRepository.save(projectMember);

        projectTemplateService.intializeProjectFromTemplate(project.getId());
        ProjectResponse response = projectMapper.toProjectResponse(project);
        System.out.println(response);
        return response;
    }

    @Override
    public List<ProjectSummaryResponse> getMyProjects() {
        Long userId = authUtil.getCurrentUserId();

        var projects =  projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.toListofProjectSummaryResponse(projects);
        }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectResponse getProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        Project project = getAccessibleProjectById(projectId,userId);

        return projectMapper.toProjectResponse(project);


    }



    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(id,userId);

        project.setName(request.name());
        project = projectRepository.save(project);

        return projectMapper.toProjectResponse(project);

    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long id) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(id,userId);


        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }



    // Internal Code

    public Project getAccessibleProjectById(Long projectId,Long userId){
        //Long userId = authUtil.getCurrentUserId();
        return projectRepository.findAccessibleProjectId(projectId,userId).orElseThrow();
    }
}
