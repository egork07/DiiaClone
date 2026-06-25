package org.example.diiaclone.service;

import jakarta.transaction.Transactional;
import org.example.diiaclone.dto.auth.AuthResponse;
import org.example.diiaclone.dto.auth.LoginRequest;
import org.example.diiaclone.dto.auth.RegisterRequest;
import org.example.diiaclone.entity.AppUser;
import org.example.diiaclone.entity.Role;
import org.example.diiaclone.entity.User;
import org.example.diiaclone.repository.AppUserRepository;
import org.example.diiaclone.repository.UserRepository;
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

    private final AppUserRepository appUserRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(AppUserRepository appUserRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.appUserRepository = appUserRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username already taken: " + request.getUsername());
        }

        Role role = appUserRepository.count() == 0 ? Role.ADMIN : Role.USER;

        AppUser appUser = new AppUser(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                role);
        appUserRepository.save(appUser);

        User domainUser = new User();
        domainUser.setUsername(request.getUsername());
        domainUser.setFullName(request.getUsername());
        domainUser.setEmail(request.getUsername() + "@diia.app");
        userRepository.save(domainUser);

        log.info("Registered user={} role={}", request.getUsername(), role);

        String token = jwtService.generateToken(appUser);
        return new AuthResponse(token, appUser.getUsername(), role.name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        AppUser appUser = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow();

        log.info("User logged in: username={}", appUser.getUsername());

        String token = jwtService.generateToken(appUser);
        return new AuthResponse(token, appUser.getUsername(),
                appUser.getRole().name());
    }
}