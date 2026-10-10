package com.resumematch.controller;

import com.resumematch.service.SampleDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/samples")
@Tag(name = "Sample Data", description = "Pre-configured sample resumes and job descriptions for rapid testing")
public class SampleController {

    private final SampleDataService sampleDataService;

    public SampleController(SampleDataService sampleDataService) {
        this.sampleDataService = sampleDataService;
    }

    @GetMapping("/java")
    @Operation(summary = "Get Senior Java Developer sample resume and job description pair")
    public ResponseEntity<Map<String, String>> getJavaSample() {
        return ResponseEntity.ok(sampleDataService.getSampleJavaPair());
    }
}
