package com.resumematch.controller;

import com.resumematch.dto.JobDescriptionDto;
import com.resumematch.dto.JobDescriptionRequest;
import com.resumematch.security.UserPrincipal;
import com.resumematch.service.JobDescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job-descriptions")
@Tag(name = "Job Descriptions", description = "Job description intake and requirement extraction")
public class JobDescriptionController {

    private final JobDescriptionService jobDescriptionService;

    public JobDescriptionController(JobDescriptionService jobDescriptionService) {
        this.jobDescriptionService = jobDescriptionService;
    }

    @PostMapping
    @Operation(summary = "Submit a target job description")
    public ResponseEntity<JobDescriptionDto> createJobDescription(
            @Valid @RequestBody JobDescriptionRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(jobDescriptionService.createJobDescription(request, userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parsed job description requirements by ID")
    public ResponseEntity<JobDescriptionDto> getJobDescription(@PathVariable Long id) {
        return ResponseEntity.ok(jobDescriptionService.getJobDescription(id));
    }
}
