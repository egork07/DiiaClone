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
        List<UserResponseDto> users = userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();

        log.info("getAllUsers: returned {} users", users.size());
        return users;
    }

    public UserResponseDto getUserById(Long id) {
        log.info("getUserById: looking for user id={}", id);

        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public UserResponseDto createUser(UserCreateDto dto) {
        log.info("createUser: creating user with email={}", dto.getEmail());
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }
        User user = UserMapper.toEntity(dto);
        UserResponseDto saved = UserMapper.toDto(userRepository.save(user));

        log.info("createUser: created user id={}", saved.getId());
        return saved;
    }

    public UserResponseDto updateUser(Long id, UserCreateDto dto) {
        log.info("updateUser: updating user id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());

        UserResponseDto updated = UserMapper.toDto(userRepository.save(user));
        log.info("updateUser: updated user id={}", id);
        return updated;
    }

    public void deleteUser(Long id) {
        log.info("deleteUser: deleting user id={}", id);

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        log.info("deleteUser: deleted user id={}", id);
    }

    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}