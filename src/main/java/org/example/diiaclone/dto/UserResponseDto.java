package org.example.diiaclone.dto;

public class UserResponseDto {

    private Long id;
    private String fullName;
    private String email;

    public UserResponseDto() {
    }

    public UserResponseDto(Long id,
                           String fullName,
                           String email) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
