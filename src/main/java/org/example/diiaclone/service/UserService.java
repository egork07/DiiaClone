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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Доступно всем авторизованным
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

    // Только ADMIN может создавать пользователей
    // @HandleAuthorizationDenied — вместо 403 вернёт null,
    // контроллер обработает это как 403 с понятным сообщением
    @PreAuthorize("hasRole('ADMIN')")
    @HandleAuthorizationDenied(handlerClass = AuthorizationDeniedHandler.class)
    public UserResponseDto createUser(UserCreateDto dto) {
        User user = UserMapper.toEntity(dto);
        return UserMapper.toDto(userRepository.save(user));
    }

    // Только ADMIN может обновлять
    @PreAuthorize("hasRole('ADMIN')")
    @HandleAuthorizationDenied(handlerClass = AuthorizationDeniedHandler.class)
    public UserResponseDto updateUser(Long id, UserCreateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed — user with id={} not found", id);
                    return new UserNotFoundException(id);
                });

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());

        return UserMapper.toDto(userRepository.save(user));
    }

    // Только ADMIN может удалять
    @PreAuthorize("hasRole('ADMIN')")
    @HandleAuthorizationDenied(handlerClass = AuthorizationDeniedHandler.class)
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