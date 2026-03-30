package com.MayukhProjects.lovable_clone.mapper;

import com.MayukhProjects.lovable_clone.dto.project.ProjectResponse;
import com.MayukhProjects.lovable_clone.dto.project.ProjectSummaryResponse;
import com.MayukhProjects.lovable_clone.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

      //@Mapping(source = "owner",target = "owner")
      ProjectResponse toProjectResponse(Project project);


    ProjectSummaryResponse toProjectSummaryResponse(Project project);

    List<ProjectSummaryResponse> toListofProjectSummaryResponse(List<Project> projects);
}
