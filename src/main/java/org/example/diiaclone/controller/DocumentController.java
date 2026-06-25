package org.example.diiaclone.controller;

import jakarta.validation.Valid;
import org.example.diiaclone.dto.DocumentCreateDto;
import org.example.diiaclone.dto.DocumentResponseDto;
import org.example.diiaclone.entity.AppUser;
import org.example.diiaclone.entity.Document;
import org.example.diiaclone.entity.User;
import org.example.diiaclone.service.DocumentService;
import org.example.diiaclone.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponseDto>> getAllDocuments(
            @AuthenticationPrincipal AppUser currentUser) {
        return ResponseEntity.ok(documentService.getAllDocuments(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDto> getDocumentById(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser currentUser) {
        return ResponseEntity.ok(documentService.getDocumentById(id, currentUser));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponseDto>> searchByType(
            @RequestParam String type,
            @AuthenticationPrincipal AppUser currentUser) {
        return ResponseEntity.ok(documentService.searchByType(type, currentUser));
    }

    @PostMapping
    public ResponseEntity<DocumentResponseDto> createDocument(
            @Valid @RequestBody DocumentCreateDto dto,
            @AuthenticationPrincipal AppUser currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.createDocument(dto, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponseDto> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentCreateDto dto,
            @AuthenticationPrincipal AppUser currentUser) {
        return ResponseEntity.ok(
                documentService.updateDocument(id, dto, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser currentUser) {
        documentService.deleteDocument(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
