package org.example.diiaclone.mapper;

import org.example.diiaclone.dto.UserCreateDto;
import org.example.diiaclone.dto.UserResponseDto;
import org.example.diiaclone.entity.User;

public class UserMapper {

    public static User toEntity(UserCreateDto dto) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public static UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }
}