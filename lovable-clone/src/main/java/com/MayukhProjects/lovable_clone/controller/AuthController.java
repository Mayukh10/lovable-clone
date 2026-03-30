package com.MayukhProjects.lovable_clone.controller;


import com.MayukhProjects.lovable_clone.dto.auth.AuthResponse;
import com.MayukhProjects.lovable_clone.dto.auth.LoginRequest;
import com.MayukhProjects.lovable_clone.dto.auth.SignUpRequest;
import com.MayukhProjects.lovable_clone.dto.auth.UserProfileResponse;
import com.MayukhProjects.lovable_clone.entity.User;
import com.MayukhProjects.lovable_clone.service.AuthService;
import com.MayukhProjects.lovable_clone.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userservice;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignUpRequest request){
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(){

        Long userId = 1L;
        return ResponseEntity.ok(userservice.getProfile(userId));
    }

}
