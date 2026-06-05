package org.example.diiaclone.controller;

import jakarta.validation.Valid;
import org.example.diiaclone.dto.DocumentCreateDto;
import org.example.diiaclone.entity.Document;
import org.example.diiaclone.entity.User;
import org.example.diiaclone.service.DocumentService;
import org.example.diiaclone.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final UserService userService;

    public DocumentController(
            DocumentService documentService,
            UserService userService) {

        this.documentService = documentService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllDocuments(Model model) {

        model.addAttribute(
                "documents",
                documentService.getAllDocuments());

        return "documents";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {

        model.addAttribute(
                "documentCreateDto",
                new DocumentCreateDto());

        model.addAttribute(
                "users",
                userService.getAllUsers());

        return "document-form";
    }

    @PostMapping
    public String createDocument(
            @Valid @ModelAttribute DocumentCreateDto dto,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "users",
                    userService.getAllUsers());

            return "document-form";
        }

        User user = userService
                .getUserById(dto.getUserId())
                .orElseThrow();

        Document document = new Document();

        document.setDocumentType(dto.getDocumentType());
        document.setDocumentNumber(dto.getDocumentNumber());
        document.setUser(user);

        documentService.saveDocument(document);

        return "redirect:/documents";
    }

    // Удаление документа
    @GetMapping("/delete/{id}")
    public String deleteDocument(@PathVariable Long id) {

        documentService.deleteDocument(id);

        return "redirect:/documents";
    }

    @GetMapping("/search")
    public String searchDocuments(
            @RequestParam String type,
            Model model) {

        model.addAttribute(
                "documents",
                documentService.searchByType(type));

        return "documents";
    }

}
