package org.example.diiaclone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DocumentCreateDto {

    @NotBlank(message = "Document type is required")
    private String documentType;

    @NotBlank(message = "Document number is required")
    @Size(min = 5, max = 30,
            message = "Document number must be from 5 to 30 characters")
    private String documentNumber;

    @NotNull(message = "User must be selected")
    private Long userId;

    public DocumentCreateDto() {
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
