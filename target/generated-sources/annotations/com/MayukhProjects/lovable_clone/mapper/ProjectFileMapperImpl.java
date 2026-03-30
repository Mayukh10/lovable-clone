package com.MayukhProjects.lovable_clone.mapper;

import com.MayukhProjects.lovable_clone.dto.project.FileNode;
import com.MayukhProjects.lovable_clone.entity.ProjectFile;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-19T22:48:18+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Azul Systems, Inc.)"
)
@Component
public class ProjectFileMapperImpl implements ProjectFileMapper {

    @Override
    public List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList) {
        if ( projectFileList == null ) {
            return null;
        }

        List<FileNode> list = new ArrayList<FileNode>( projectFileList.size() );
        for ( ProjectFile projectFile : projectFileList ) {
            list.add( projectFileToFileNode( projectFile ) );
        }

        return list;
    }

    protected FileNode projectFileToFileNode(ProjectFile projectFile) {
        if ( projectFile == null ) {
            return null;
        }

        String path = null;

        path = projectFile.getPath();

        Instant modifiedAt = null;
        Long size = null;
        String type = null;

        FileNode fileNode = new FileNode( path, modifiedAt, size, type );

        return fileNode;
    }
}
