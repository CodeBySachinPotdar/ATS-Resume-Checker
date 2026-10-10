package com.resumematch.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "analysis_histories")
public class AnalysisHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "analysis_id", nullable = false)
    private Long analysisId;

    @Column(name = "resume_title", length = 150)
    private String resumeTitle;

    @Column(name = "job_title", length = 150)
    private String jobTitle;

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt = Instant.now();

    public AnalysisHistory() {}

    public AnalysisHistory(Long userId, Long analysisId, String resumeTitle, String jobTitle, String companyName, Double overallScore) {
        this.userId = userId;
        this.analysisId = analysisId;
        this.resumeTitle = resumeTitle;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.overallScore = overallScore;
        this.recordedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getAnalysisId() { return analysisId; }
    public void setAnalysisId(Long analysisId) { this.analysisId = analysisId; }

    public String getResumeTitle() { return resumeTitle; }
    public void setResumeTitle(String resumeTitle) { this.resumeTitle = resumeTitle; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }

    public Instant getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Instant recordedAt) { this.recordedAt = recordedAt; }
}
