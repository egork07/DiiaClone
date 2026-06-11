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
        List<DocumentResponseDto> docs = documentRepository.findAll()
                .stream()
                .map(DocumentMapper::toDto)
                .toList();

        log.info("getAllDocuments: returned {} documents", docs.size());
        return docs;
    }

    public DocumentResponseDto getDocumentById(Long id) {
        log.info("getDocumentById: looking for document id={}", id);

        return documentRepository.findById(id)
                .map(DocumentMapper::toDto)
                .orElseThrow(() -> new DocumentNotFoundException(id));
    }

    public List<DocumentResponseDto> searchByType(String type) {
        log.info("searchByType: searching for type='{}'", type);

        List<DocumentResponseDto> results = documentRepository
                .findByDocumentTypeContainingIgnoreCase(type)
                .stream()
                .map(DocumentMapper::toDto)
                .toList();

        log.info("searchByType: found {} results for type='{}'", results.size(), type);
        return results;
    }

    public DocumentResponseDto createDocument(DocumentCreateDto dto) {
        log.info("createDocument: creating document type={} for userId={}",
                dto.getDocumentType(), dto.getUserId());

        // UserNotFoundException бросится здесь, если userId не существует
        User user = userService.getUserEntityById(dto.getUserId());

        Document document = new Document();
        document.setDocumentType(dto.getDocumentType());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setUser(user);

        DocumentResponseDto saved = DocumentMapper.toDto(documentRepository.save(document));
        log.info("createDocument: created document id={}", saved.getId());
        return saved;
    }

    public DocumentResponseDto updateDocument(Long id, DocumentCreateDto dto) {
        log.info("updateDocument: updating document id={}", id);

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(id));

        User user = userService.getUserEntityById(dto.getUserId());

        document.setDocumentType(dto.getDocumentType());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setUser(user);

        DocumentResponseDto updated = DocumentMapper.toDto(documentRepository.save(document));
        log.info("updateDocument: updated document id={}", id);
        return updated;
    }

    public void deleteDocument(Long id) {
        log.info("deleteDocument: deleting document id={}", id);

        if (!documentRepository.existsById(id)) {
            throw new DocumentNotFoundException(id);
        }

        documentRepository.deleteById(id);
        log.info("deleteDocument: deleted document id={}", id);
    }
}