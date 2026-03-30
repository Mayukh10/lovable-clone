package com.MayukhProjects.lovable_clone.mapper;

import com.MayukhProjects.lovable_clone.dto.auth.SignUpRequest;
import com.MayukhProjects.lovable_clone.dto.auth.UserProfileResponse;
import com.MayukhProjects.lovable_clone.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
     User toEntity(SignUpRequest signUpRequest);

     UserProfileResponse toUserProfileResponse(User user);
}
