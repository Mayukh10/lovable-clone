package com.MayukhProjects.lovable_clone.error;


import lombok.RequiredArgsConstructor;


public class BadRequestException extends RuntimeException {

    public BadRequestException(String message){
        super(message);
    }
}
