package org.example.diiaclone.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class SecurityErrorHandler
        implements org.springframework.security.web.AuthenticationEntryPoint,
        org.springframework.security.web.access.AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(SecurityErrorHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules(); // для LocalDateTime

    // 401 Unauthorized
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        log.warn("Unauthorized access to {}: {}", request.getRequestURI(),
                authException.getMessage());

        writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized", "Valid JWT token is required");
    }

    // 403 Forbidden
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {

        log.warn("Access denied to {} for user: {}",
                request.getRequestURI(), accessDeniedException.getMessage());

        writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                "Forbidden", "You don't have permission to access this resource");
    }

    private void writeJson(HttpServletResponse response,
                           int status, String error, String message)
            throws IOException {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = Map.of(
                "status", status,
                "error", error,
                "message", message,
                "timestamp", LocalDateTime.now().toString());

        objectMapper.writeValue(response.getWriter(), body);
    }
}
