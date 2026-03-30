package com.MayukhProjects.lovable_clone.error;


import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(apiError.status()).body(apiError);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException ex) {

        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getResourceName() + " with id " + ex.getResourceId() + " not found"
        );

        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInpuValidationError(MethodArgumentNotValidException ex) {

        List<ApiFieldErrors> errors =   ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiFieldErrors(error.getField(), error.getDefaultMessage()))
                .toList();

       ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Input validation Failed",errors);
       log.error(apiError.toString(),ex);
       return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildResponse(
                        HttpStatus.NOT_FOUND,
                        "User not found",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildResponse(
                        HttpStatus.UNAUTHORIZED,
                        "Authentication failed",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildResponse(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid or expired JWT token",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildResponse(
                        HttpStatus.FORBIDDEN,
                        "Access denied",
                        ex.getMessage()
                ));
    }

    // Fallback (optional but recommended)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal server error",
                        ex.getMessage()
                ));
    }
    private Map<String, Object> buildResponse(
            HttpStatus status,
            String error,
            String message
    ) {
        return Map.of(
                "timestamp", Instant.now(),
                "status", status.value(),
                "error", error,
                "message", message
        );
    }
}


