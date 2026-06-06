package org.example.diiaclone.mapper;

import org.example.diiaclone.dto.DocumentResponseDto;
import org.example.diiaclone.entity.Document;

public class DocumentMapper {

    public static DocumentResponseDto toDto(Document document) {
        return new DocumentResponseDto(
                document.getId(),
                document.getDocumentType(),
                document.getDocumentNumber(),
                document.getUser().getFullName()
        );
    }
}
