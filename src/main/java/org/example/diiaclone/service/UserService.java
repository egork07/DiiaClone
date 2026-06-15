package org.example.diiaclone.service;

import org.example.diiaclone.dto.UserCreateDto;
import org.example.diiaclone.dto.UserResponseDto;
import org.example.diiaclone.entity.User;
import org.example.diiaclone.exeption.EmailAlreadyExistsException;
import org.example.diiaclone.exeption.UserNotFoundException;
import org.example.diiaclone.mapper.UserMapper;
import org.example.diiaclone.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

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

    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("User with id={} not found", id);
                    return new UserNotFoundException(id);
                });
    }

    public UserResponseDto createUser(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email already exists: {}", dto.getEmail());
            throw new EmailAlreadyExistsException(dto.getEmail());
        }
        User user = UserMapper.toEntity(dto);
        return UserMapper.toDto(userRepository.save(user));
    }

    public UserResponseDto updateUser(Long id, UserCreateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed — user with id={} not found", id);
                    return new UserNotFoundException(id);
                });

        // Проверяем email только если он изменился
        if (!user.getEmail().equals(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email already exists: {}", dto.getEmail());
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        return UserMapper.toDto(userRepository.save(user));}

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("Delete failed — user with id={} not found", id);
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("getUserEntityById — user with id={} not found", id);
                    return new UserNotFoundException(id);
                });
    }
}