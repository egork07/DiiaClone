package org.example.diiaclone.service;

import org.example.diiaclone.dto.DocumentCreateDto;
import org.example.diiaclone.dto.DocumentResponseDto;
import org.example.diiaclone.entity.Document;
import org.example.diiaclone.entity.User;
import org.example.diiaclone.mapper.DocumentMapper;
import org.example.diiaclone.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserService userService;

    public DocumentService(
            DocumentRepository documentRepository,
            UserService userService) {

        this.documentRepository = documentRepository;
        this.userService = userService;
    }

    public List<DocumentResponseDto> getAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(DocumentMapper::toDto)
                .toList();
    }

    public Optional<DocumentResponseDto> getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(DocumentMapper::toDto);
    }

    public List<DocumentResponseDto> searchByType(String type) {
        return documentRepository
                .findByDocumentTypeContainingIgnoreCase(type)
                .stream()
                .map(DocumentMapper::toDto)
                .toList();
    }

    public DocumentResponseDto createDocument(DocumentCreateDto dto) {
        User user = userService.getUserEntityById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + dto.getUserId()));

        Document document = new Document();
        document.setDocumentType(dto.getDocumentType());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setUser(user);

        return DocumentMapper.toDto(documentRepository.save(document));
    }

    public Optional<DocumentResponseDto> updateDocument(Long id, DocumentCreateDto dto) {
        return documentRepository.findById(id)
                .map(document -> {
                    User user = userService.getUserEntityById(dto.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "User not found with id: " + dto.getUserId()));

                    document.setDocumentType(dto.getDocumentType());
                    document.setDocumentNumber(dto.getDocumentNumber());
                    document.setUser(user);

                    return DocumentMapper.toDto(documentRepository.save(document));
                });
    }

    public boolean deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            return false;
        }
        documentRepository.deleteById(id);
        return true;
    }
}