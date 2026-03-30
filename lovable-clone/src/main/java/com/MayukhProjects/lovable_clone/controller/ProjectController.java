package com.MayukhProjects.lovable_clone.controller;


import com.MayukhProjects.lovable_clone.dto.project.ProjectRequest;
import com.MayukhProjects.lovable_clone.dto.project.ProjectResponse;
import com.MayukhProjects.lovable_clone.dto.project.ProjectSummaryResponse;
import com.MayukhProjects.lovable_clone.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

     private final ProjectService projectService;

     @GetMapping()
     public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects(){
         return ResponseEntity.ok(projectService.getMyProjects());
     }
    // Hard coded without spring security use as to create service layer first
    //@GetMapping()
    //public ResponseEntity<List<ProjectSummaryResponse>> getMyProjects(){
       // Long userId = 1L;
        //return ResponseEntity.ok(projectService.getMyProjects(userId));
    //}

     @GetMapping("/{id}")
     public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id){

         return ResponseEntity.ok(projectService.getProjectById(id));
     }

     @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest request){

         return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
     }
     @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@RequestBody @Valid ProjectRequest request,@PathVariable Long id){

         return ResponseEntity.ok(projectService.updateProject(id,request));
     }

     @DeleteMapping("/{id}")
     public ResponseEntity<Void> softDelete(@PathVariable Long id){

         projectService.softDelete(id);
         return ResponseEntity.noContent().build();

     }









}
