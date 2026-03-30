package com.MayukhProjects.lovable_clone.mapper;

import com.MayukhProjects.lovable_clone.dto.auth.SignUpRequest;
import com.MayukhProjects.lovable_clone.dto.auth.UserProfileResponse;
import com.MayukhProjects.lovable_clone.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-18T22:09:24+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.9 (Azul Systems, Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(SignUpRequest signUpRequest) {
        if ( signUpRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.name( signUpRequest.name() );
        user.username( signUpRequest.username() );
        user.password( signUpRequest.password() );

        return user.build();
    }

    @Override
    public UserProfileResponse toUserProfileResponse(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String username = null;
        String name = null;

        id = user.getId();
        username = user.getUsername();
        name = user.getName();

        UserProfileResponse userProfileResponse = new UserProfileResponse( id, username, name );

        return userProfileResponse;
    }
}
