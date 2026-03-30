package com.MayukhProjects.lovable_clone.service;

import com.MayukhProjects.lovable_clone.dto.auth.AuthResponse;
import com.MayukhProjects.lovable_clone.dto.auth.LoginRequest;
import com.MayukhProjects.lovable_clone.dto.auth.SignUpRequest;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    AuthResponse signup(SignUpRequest request);

     AuthResponse login(LoginRequest request);
}
