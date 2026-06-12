package org.example.diiaclone.service;

import org.example.diiaclone.dto.DocumentCreateDto;
import org.example.diiaclone.dto.DocumentResponseDto;
import org.example.diiaclone.entity.Document;
import org.example.diiaclone.entity.User;
import org.example.diiaclone.exeption.DocumentNotFoundException;
import org.example.diiaclone.mapper.DocumentMapper;
import org.example.diiaclone.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

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

    public DocumentResponseDto getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(DocumentMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Document with id={} not found", id);
                    return new DocumentNotFoundException(id);
                });
    }

    public List<DocumentResponseDto> searchByType(String type) {
        return documentRepository
                .findByDocumentTypeContainingIgnoreCase(type)
                .stream()
                .map(DocumentMapper::toDto)
                .toList();
    }

    public DocumentResponseDto createDocument(DocumentCreateDto dto) {
        User user = userService.getUserEntityById(dto.getUserId());

        Document document = new Document();
        document.setDocumentType(dto.getDocumentType());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setUser(user);

        return DocumentMapper.toDto(documentRepository.save(document));
    }

    public DocumentResponseDto updateDocument(Long id, DocumentCreateDto dto) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed — document with id={} not found", id);
                    return new DocumentNotFoundException(id);
                });

        User user = userService.getUserEntityById(dto.getUserId());

        document.setDocumentType(dto.getDocumentType());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setUser(user);

        return DocumentMapper.toDto(documentRepository.save(document));
    }

    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            log.warn("Delete failed — document with id={} not found", id);
            throw new DocumentNotFoundException(id);
        }
        documentRepository.deleteById(id);
    }
}