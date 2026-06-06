package org.example.diiaclone.dto;

public class DocumentResponseDto {

    private Long id;
    private String documentType;
    private String documentNumber;
    private String ownerName;

    public DocumentResponseDto() {}

    public DocumentResponseDto(Long id, String documentType,
                               String documentNumber, String ownerName) {
        this.id = id;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.ownerName = ownerName;
    }

    public Long getId() { return id; }
    public String getDocumentType() { return documentType; }
    public String getDocumentNumber() { return documentNumber; }
    public String getOwnerName() { return ownerName; }
    public void setId(Long id) { this.id = id; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}