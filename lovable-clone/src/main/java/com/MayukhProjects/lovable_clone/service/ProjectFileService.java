package com.MayukhProjects.lovable_clone.service;

import com.MayukhProjects.lovable_clone.dto.project.FileContentResponse;
import com.MayukhProjects.lovable_clone.dto.project.FileNode;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectFileService {
     List<FileNode> getFileTree(Long projectId);

    @Nullable FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(java.lang.Long projectId, java.lang.String filepath, java.lang.String fileContent);
}
