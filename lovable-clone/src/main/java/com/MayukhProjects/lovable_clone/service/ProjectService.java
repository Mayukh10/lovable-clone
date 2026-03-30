package com.MayukhProjects.lovable_clone.service;

import com.MayukhProjects.lovable_clone.dto.project.ProjectRequest;
import com.MayukhProjects.lovable_clone.dto.project.ProjectResponse;
import com.MayukhProjects.lovable_clone.dto.project.ProjectSummaryResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getMyProjects();

     ProjectResponse getProjectById(Long id);

     ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(Long id, ProjectRequest request);

     //Void deleteProject(Long id);

    //void softDelete(Long id, Long usedId);


    void softDelete(Long id);


}
