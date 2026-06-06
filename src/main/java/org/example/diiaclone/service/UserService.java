package org.example.diiaclone.service;

import org.example.diiaclone.dto.UserCreateDto;
import org.example.diiaclone.dto.UserResponseDto;
import org.example.diiaclone.entity.User;
import org.example.diiaclone.mapper.UserMapper;
import org.example.diiaclone.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto);
    }

    public UserResponseDto createUser(UserCreateDto dto) {
        User user = UserMapper.toEntity(dto);
        return UserMapper.toDto(userRepository.save(user));
    }

    public Optional<UserResponseDto> updateUser(Long id, UserCreateDto dto) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFullName(dto.getFullName());
                    user.setEmail(dto.getEmail());
                    return UserMapper.toDto(userRepository.save(user));
                });
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    // Используется в DocumentService
    public Optional<User> getUserEntityById(Long id) {
        return userRepository.findById(id);
    }
}
