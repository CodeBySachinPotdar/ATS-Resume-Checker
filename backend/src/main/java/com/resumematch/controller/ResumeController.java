package com.resumematch.controller;

import com.resumematch.dto.ResumeDto;
import com.resumematch.dto.ResumeTextRequest;
import com.resumematch.security.UserPrincipal;
import com.resumematch.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resumes")
@Tag(name = "Resumes", description = "Resume file upload, text intake, parsing and management")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload resume file (PDF, DOC, DOCX, TXT)")
    public ResponseEntity<ResumeDto> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(resumeService.uploadResumeFile(file, userId));
    }

    @PostMapping("/text")
    @Operation(summary = "Paste resume content as raw text")
    public ResponseEntity<ResumeDto> pasteResumeText(
            @Valid @RequestBody ResumeTextRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(resumeService.saveResumeText(request, userId));
    }

    @GetMapping
    @Operation(summary = "List all resumes belonging to the authenticated user")
    public ResponseEntity<List<ResumeDto>> getUserResumes(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getId() : 1L;
        return ResponseEntity.ok(resumeService.getUserResumes(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve resume details by ID")
    public ResponseEntity<ResumeDto> getResume(@PathVariable Long id) {
        return ResponseEntity.ok(resumeService.getResume(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resume by ID")
    public ResponseEntity<Map<String, Object>> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Resume deleted successfully."));
    }
}
