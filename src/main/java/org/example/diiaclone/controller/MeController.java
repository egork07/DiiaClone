package org.example.diiaclone.controller;

import org.example.diiaclone.dto.auth.AuthResponse;
import org.example.diiaclone.entity.AppUser;
import org.example.diiaclone.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class MeController {

    private final JwtService jwtService;

    public MeController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(
            @AuthenticationPrincipal AppUser user) {

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(
                new AuthResponse(token, user.getUsername(), user.getRole().name()));
    }
}