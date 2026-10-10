package com.resumematch.dto;

import jakarta.validation.constraints.NotNull;

public class AnalysisRequest {

    @NotNull(message = "Resume ID is required")
    private Long resumeId;

    @NotNull(message = "Job Description ID is required")
    private Long jobDescriptionId;

    public AnalysisRequest() {}

    public AnalysisRequest(Long resumeId, Long jobDescriptionId) {
        this.resumeId = resumeId;
        this.jobDescriptionId = jobDescriptionId;
    }

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public Long getJobDescriptionId() { return jobDescriptionId; }
    public void setJobDescriptionId(Long jobDescriptionId) { this.jobDescriptionId = jobDescriptionId; }
}
