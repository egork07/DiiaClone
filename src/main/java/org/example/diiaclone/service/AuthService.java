package org.example.diiaclone.service;

import org.example.diiaclone.dto.auth.AuthResponse;
import org.example.diiaclone.dto.auth.LoginRequest;
import org.example.diiaclone.dto.auth.RegisterRequest;
import org.example.diiaclone.entity.AppUser;
import org.example.diiaclone.entity.Role;
import org.example.diiaclone.repository.AppUserRepository;
import org.example.diiaclone.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username already taken: " + request.getUsername());
        }

        Role role = userRepository.count() == 0 ? Role.ADMIN : Role.USER;

        AppUser user = new AppUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()), // BCrypt
                role);

        userRepository.save(user);
        log.info("Registered new user={} role={}", user.getUsername(), role);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUsername(), role.name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();

        log.info("User logged in: username={}", user.getUsername());

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
