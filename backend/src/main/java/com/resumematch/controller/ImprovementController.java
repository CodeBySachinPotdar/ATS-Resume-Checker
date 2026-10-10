package com.resumematch.controller;

import com.resumematch.dto.OptimizationResponseDto;
import com.resumematch.entity.Resume;
import com.resumematch.service.ImprovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/resumes")
@Tag(name = "Resume Improvements", description = "AI-powered bullet refinement, summary optimization and versioning")
public class ImprovementController {

    private final ImprovementService improvementService;

    public ImprovementController(ImprovementService improvementService) {
        this.improvementService = improvementService;
    }

    @PostMapping("/{id}/improvements")
    @Operation(summary = "Generate actionable bullet point improvements and summary optimization")
    public ResponseEntity<OptimizationResponseDto> getImprovements(
            @PathVariable Long id,
            @RequestParam(value = "targetRole", required = false) String targetRole
    ) {
        return ResponseEntity.ok(improvementService.getImprovements(id, targetRole));
    }

    @PostMapping("/{id}/optimize")
    @Operation(summary = "Apply selected improvement suggestions and create a new resume version")
    public ResponseEntity<Map<String, Object>> optimizeResume(
            @PathVariable Long id,
            @RequestBody Map<String, String> appliedChanges
    ) {
        Resume updated = improvementService.optimizeAndSaveVersion(id, appliedChanges);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "resumeId", updated.getId(),
                "versionCount", updated.getVersions().size(),
                "message", "Resume version created successfully with applied optimizations."
        ));
    }
}
