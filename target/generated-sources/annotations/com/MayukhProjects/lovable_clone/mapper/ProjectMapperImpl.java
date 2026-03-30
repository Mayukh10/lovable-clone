package com.MayukhProjects.lovable_clone.mapper;

import com.MayukhProjects.lovable_clone.dto.project.ProjectResponse;
import com.MayukhProjects.lovable_clone.dto.project.ProjectSummaryResponse;
import com.MayukhProjects.lovable_clone.entity.Project;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-18T22:09:24+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Azul Systems, Inc.)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Override
    public ProjectResponse toProjectResponse(Project project) {
        if ( project == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        id = project.getId();
        name = project.getName();
        createdAt = project.getCreatedAt();
        updatedAt = project.getUpdatedAt();

        ProjectResponse projectResponse = new ProjectResponse( id, name, createdAt, updatedAt );

        return projectResponse;
    }

    @Override
    public ProjectSummaryResponse toProjectSummaryResponse(Project project) {
        if ( project == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        id = project.getId();
        name = project.getName();
        createdAt = project.getCreatedAt();
        updatedAt = project.getUpdatedAt();

        ProjectSummaryResponse projectSummaryResponse = new ProjectSummaryResponse( id, name, createdAt, updatedAt );

        return projectSummaryResponse;
    }

    @Override
    public List<ProjectSummaryResponse> toListofProjectSummaryResponse(List<Project> projects) {
        if ( projects == null ) {
            return null;
        }

        List<ProjectSummaryResponse> list = new ArrayList<ProjectSummaryResponse>( projects.size() );
        for ( Project project : projects ) {
            list.add( toProjectSummaryResponse( project ) );
        }

        return list;
    }
}
