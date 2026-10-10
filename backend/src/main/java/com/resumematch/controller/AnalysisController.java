package com.resumematch.controller;

import com.resumematch.dto.*;
import com.resumematch.security.UserPrincipal;
import com.resumematch.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analyses")
@Tag(name = "ATS Analyses", description = "ATS evaluation, score breakdown, keyword matching and recommendations")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    @Operation(summary = "Trigger asynchronous ATS analysis on an existing resume and job description")
    public ResponseEntity<AnalysisResponseDto> createAnalysis(
            @Valid @RequestBody AnalysisRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(analysisService.createAnalysis(request, userId));
    }

    @PostMapping("/quick-check")
    @Operation(summary = "Perform immediate ATS analysis directly from resume text and job description")
    public ResponseEntity<AnalysisResponseDto> quickCheck(
            @Valid @RequestBody QuickAnalysisRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(analysisService.runQuickAnalysis(request, userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complete ATS analysis report and status by ID")
    public ResponseEntity<AnalysisResponseDto> getAnalysis(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.getAnalysis(id));
    }

    @GetMapping("/{id}/score")
    @Operation(summary = "Get detailed 8-category ATS score breakdown")
    public ResponseEntity<ScoreBreakdownDto> getScoreBreakdown(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.getScoreBreakdown(id));
    }

    @GetMapping("/{id}/keywords")
    @Operation(summary = "Get matched, related, and missing keyword breakdown")
    public ResponseEntity<List<KeywordMatchDto>> getKeywords(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.getKeywords(id));
    }

    @GetMapping("/{id}/recommendations")
    @Operation(summary = "Get prioritized actionable recommendations with anti-fraud guidance")
    public ResponseEntity<List<RecommendationDto>> getRecommendations(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.getRecommendations(id));
    }

    @GetMapping
    @Operation(summary = "List historical analyses for the current authenticated user")
    public ResponseEntity<List<AnalysisResponseDto>> getUserAnalyses(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal != null ? principal.getId() : 1L;
        return ResponseEntity.ok(analysisService.getUserAnalyses(userId));
    }
}
