package com.MayukhProjects.lovable_clone.service;

import com.MayukhProjects.lovable_clone.dto.auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;


public interface UserService {

   UserProfileResponse getProfile(Long userId);
}
