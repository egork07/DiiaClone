package org.example.diiaclone.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {}

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setUsername(String u) { this.username = u; }
    public void setPassword(String p) { this.password = p; }
}
