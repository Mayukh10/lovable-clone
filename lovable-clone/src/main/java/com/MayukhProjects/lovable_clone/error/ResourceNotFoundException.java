package com.MayukhProjects.lovable_clone.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {
     private String resourceName;
     private String resourceId;

    public ResourceNotFoundException(String resourceName, String resourceId) {
        super(resourceName + " with id " + resourceId + " not found");
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }
}
