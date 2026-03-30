package com.MayukhProjects.lovable_clone.service.impl;

import com.MayukhProjects.lovable_clone.entity.Project;
import com.MayukhProjects.lovable_clone.entity.ProjectFile;
import com.MayukhProjects.lovable_clone.error.ResourceNotFoundException;
import com.MayukhProjects.lovable_clone.repository.ProjectFileRepository;
import com.MayukhProjects.lovable_clone.repository.ProjectRepository;
import com.MayukhProjects.lovable_clone.service.ProjectTemplateService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectTemplateServiceimpl implements ProjectTemplateService {

    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;

    private static final String TEMPLATE_BUCKET = "starter-projects";
    private static final String TARGET_BUCKET = "projects";
    private static final String TEMPLATE_NAME = "react-vite-tailwind-daisyui-starter-main";

    @Override
    public void intializeProjectFromTemplate(Long projectId) {

        log.info("🔥 Initializing project from template: {}", projectId);

        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new ResourceNotFoundException("Project", projectId.toString()));

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(TEMPLATE_BUCKET)
                            .prefix(TEMPLATE_NAME + "/")
                            .recursive(true)
                            .build()
            );

            List<ProjectFile> filesToSave = new ArrayList<>(); // for metadata in postgres db

            for (Result<Item> result : results) {
                Item item = result.get();
                String sourceKey = item.objectName();

                String cleanPath = sourceKey.replaceFirst(TEMPLATE_NAME + "/", "");
                String destKey = projectId + "/" + cleanPath;

                log.info("📂 Source: {}", sourceKey);
                log.info("📂 Clean Path: {}", cleanPath);
                log.info("📂 Destination: {}", destKey);


                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(TARGET_BUCKET)
                                .object(destKey)
                                .source(
                                        CopySource.builder()
                                                .bucket(TEMPLATE_BUCKET)
                                                .object(sourceKey)
                                                .build()
                                )
                                .build()
                );

                ProjectFile pf = ProjectFile.builder()
                        .project(project)
                        .path(cleanPath)
                        .minioObjKey(destKey)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                filesToSave.add(pf);
            }

            projectFileRepository.saveAll(filesToSave);
            log.info("✅ Template copied successfully for project {}", projectId);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize project from template", e);
        }
    }

}

