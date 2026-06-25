package org.example.diiaclone.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public RegisterRequest() {}

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setUsername(String u) { this.username = u; }
    public void setPassword(String p) { this.password = p; }
}