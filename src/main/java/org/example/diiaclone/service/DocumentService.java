package org.example.diiaclone.service;

import org.example.diiaclone.dto.DocumentCreateDto;
import org.example.diiaclone.dto.DocumentResponseDto;
import org.example.diiaclone.entity.AppUser;
import org.example.diiaclone.entity.Document;
import org.example.diiaclone.entity.Role;
import org.example.diiaclone.entity.User;
import org.example.diiaclone.exeption.DocumentNotFoundException;
import org.example.diiaclone.mapper.DocumentMapper;
import org.example.diiaclone.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final UserService userService;

    public DocumentService(DocumentRepository documentRepository,
                           UserService userService) {
        this.documentRepository = documentRepository;
        this.userService = userService;
    }

    public List<DocumentResponseDto> getAllDocuments(AppUser currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return documentRepository.findAll()
                    .stream().map(DocumentMapper::toDto).toList();
        }

        Long domainUserId = getDomainUserId(currentUser.getUsername());
        return documentRepository.findByUserId(domainUserId)
                .stream().map(DocumentMapper::toDto).toList();
    }

    public List<DocumentResponseDto> searchByType(String type, AppUser currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return documentRepository
                    .findByDocumentTypeContainingIgnoreCase(type)
                    .stream().map(DocumentMapper::toDto).toList();
        }

        Long domainUserId = getDomainUserId(currentUser.getUsername());
        return documentRepository
                .findByUserIdAndDocumentTypeContainingIgnoreCase(domainUserId, type)
                .stream().map(DocumentMapper::toDto).toList();
    }

    public DocumentResponseDto getDocumentById(Long id, AppUser currentUser) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Document with id={} not found", id);
                    return new DocumentNotFoundException(id);
                });

        checkOwnership(doc, currentUser);
        return DocumentMapper.toDto(doc);
    }

    public DocumentResponseDto createDocument(DocumentCreateDto dto,
                                              AppUser currentUser) {
        Long targetUserId;

        if (currentUser.getRole() == Role.ADMIN) {
            if (dto.getUserId() == null) {
                throw new IllegalArgumentException("Please select a user for this document");
            }
            targetUserId = dto.getUserId();
        } else {
            targetUserId = getDomainUserId(currentUser.getUsername());
        }

        User user = userService.getUserEntityById(targetUserId);

        Document document = new Document();
        document.setDocumentType(dto.getDocumentType());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setUser(user);

        return DocumentMapper.toDto(documentRepository.save(document));
    }

    public DocumentResponseDto updateDocument(Long id, DocumentCreateDto dto,
                                              AppUser currentUser) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Document with id={} not found", id);
                    return new DocumentNotFoundException(id);
                });

        checkOwnership(document, currentUser);

        Long targetUserId = dto.getUserId();
        if (currentUser.getRole() != Role.ADMIN) {
            targetUserId = getDomainUserId(currentUser.getUsername());
        }

        User user = userService.getUserEntityById(targetUserId);
        document.setDocumentType(dto.getDocumentType());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setUser(user);

        return DocumentMapper.toDto(documentRepository.save(document));
    }

    public void deleteDocument(Long id, AppUser currentUser) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Document with id={} not found", id);
                    return new DocumentNotFoundException(id);
                });

        checkOwnership(document, currentUser);
        documentRepository.deleteById(id);
    }

    private void checkOwnership(Document document, AppUser currentUser) {
        if (currentUser.getRole() == Role.ADMIN) return;

        Long domainUserId = getDomainUserId(currentUser.getUsername());
        if (!document.getUser().getId().equals(domainUserId)) {
            log.warn("Access denied: user={} tried to access document={}",
                    currentUser.getUsername(), document.getId());
            throw new AccessDeniedException("You don't own this document");
        }
    }

    private Long getDomainUserId(String username) {
        return userService.getUserEntityByUsername(username).getId();
    }
}