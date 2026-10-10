package com.resumematch.controller;

import com.resumematch.domain.TemplateType;
import com.resumematch.entity.Resume;
import com.resumematch.export.ResumeExportService;
import com.resumematch.parser.ResumeTextParserHelper;
import com.resumematch.parser.model.ResumeContent;
import com.resumematch.repository.ResumeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/resumes")
@Tag(name = "Resume Export", description = "Export ATS-friendly resumes in PDF, DOCX, or TXT formats across 4 templates")
public class ExportController {

    private final ResumeRepository resumeRepository;
    private final ResumeExportService exportService;

    public ExportController(ResumeRepository resumeRepository, ResumeExportService exportService) {
        this.resumeRepository = resumeRepository;
        this.exportService = exportService;
    }

    @GetMapping("/{id}/export")
    @Operation(summary = "Export saved resume into PDF, DOCX, or TXT format with selected template")
    public ResponseEntity<byte[]> exportResume(
            @PathVariable Long id,
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam(defaultValue = "CLASSIC_PROFESSIONAL") TemplateType template
    ) {
        Resume resume = resumeRepository.findById(id).orElseThrow();
        byte[] bytes = exportService.export(resume, format, template);

        String filename = sanitizeFilename(resume.getTitle()) + "." + format.toLowerCase();
        return buildFileResponse(bytes, format, filename);
    }

    @PostMapping("/export-preview")
    @Operation(summary = "Export arbitrary resume text directly into PDF, DOCX, or TXT format")
    public ResponseEntity<byte[]> exportPreview(
            @RequestBody Map<String, String> payload,
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam(defaultValue = "CLASSIC_PROFESSIONAL") TemplateType template
    ) {
        String text = payload.getOrDefault("text", "");
        ResumeContent content = ResumeTextParserHelper.parseFromText(text);
        byte[] bytes = exportService.exportContent(content, format, template);

        String candidateName = content.getContactInfo().getName() != null ? content.getContactInfo().getName() : "Resume";
        String filename = sanitizeFilename(candidateName) + "_ATS." + format.toLowerCase();
        return buildFileResponse(bytes, format, filename);
    }

    private ResponseEntity<byte[]> buildFileResponse(byte[] bytes, String format, String filename) {
        MediaType mediaType = switch (format.toLowerCase()) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "docx" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            default -> MediaType.TEXT_PLAIN;
        };

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }

    private String sanitizeFilename(String name) {
        if (name == null || name.isBlank()) return "resume";
        return name.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
