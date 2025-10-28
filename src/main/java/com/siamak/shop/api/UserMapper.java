package com.siamak.shop.api;

import com.siamak.shop.client.user.UserDto;
import com.siamak.shop.model.User;

public class UserMapper {

    // Convert entity -> DTO
    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }

    // Convert DTO -> entity
    public static User toEntity(UserDto dto) {
        return User.builder()
                .id(dto.id())             // use record getter
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .role(dto.role())
                .build();
    }
}
