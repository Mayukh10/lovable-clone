package com.MayukhProjects.lovable_clone.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public record ApiError(
     HttpStatus status,
    String message,
    Instant timestame,
     @JsonInclude(JsonInclude.Include.NON_NULL) List<ApiFieldErrors> errors
    ){
    public ApiError(HttpStatus status, String message){
        this(status,message,Instant.now(), null);
    }
    public ApiError(HttpStatus status, String message, List<ApiFieldErrors> errors){
        this(status,message,Instant.now(), errors);
    }
}

record ApiFieldErrors(String field,String message){}
