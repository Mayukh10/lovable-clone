package com.MayukhProjects.lovable_clone.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @Email @NotBlank String username,
        @Size(min= 1) @NotBlank String name,
        @Size(min = 1) String password
) {
}
