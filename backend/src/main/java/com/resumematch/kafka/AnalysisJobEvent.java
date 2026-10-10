package com.resumematch.kafka;

import java.io.Serializable;
import java.time.Instant;

public class AnalysisJobEvent implements Serializable {

    private Long analysisId;
    private Long resumeId;
    private Long jobDescriptionId;
    private Long userId;
    private Instant timestamp;

    public AnalysisJobEvent() {}

    public AnalysisJobEvent(Long analysisId, Long resumeId, Long jobDescriptionId, Long userId) {
        this.analysisId = analysisId;
        this.resumeId = resumeId;
        this.jobDescriptionId = jobDescriptionId;
        this.userId = userId;
        this.timestamp = Instant.now();
    }

    public Long getAnalysisId() { return analysisId; }
    public void setAnalysisId(Long analysisId) { this.analysisId = analysisId; }

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public Long getJobDescriptionId() { return jobDescriptionId; }
    public void setJobDescriptionId(Long jobDescriptionId) { this.jobDescriptionId = jobDescriptionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
