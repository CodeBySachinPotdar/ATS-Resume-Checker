package com.resumematch.entity;

import com.resumematch.domain.AnalysisStatus;
import com.resumematch.domain.AtsCategory;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resume_analyses")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_description_id", nullable = false)
    private JobDescription jobDescription;

    @Column(name = "overall_score", nullable = false)
    private Double overallScore = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "score_category", nullable = false, length = 30)
    private AtsCategory scoreCategory = AtsCategory.POOR_MATCH;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AnalysisStatus status = AnalysisStatus.PENDING;

    @Column(name = "formatting_score")
    private Double formattingScore = 100.0;

    @Lob
    @Column(name = "formatting_issues_json", columnDefinition = "TEXT")
    private String formattingIssuesJson;

    @Lob
    @Column(name = "strengths_json", columnDefinition = "TEXT")
    private String strengthsJson;

    @Lob
    @Column(name = "gaps_json", columnDefinition = "TEXT")
    private String gapsJson;

    @Lob
    @Column(name = "section_scores_json", columnDefinition = "TEXT")
    private String sectionScoresJson;

    @Column(name = "processing_duration_ms")
    private Long processingDurationMs;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToOne(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private ScoreBreakdown scoreBreakdown;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.Set<KeywordMatch> keywordMatches = new java.util.LinkedHashSet<>();

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.Set<Recommendation> recommendations = new java.util.LinkedHashSet<>();

    public ResumeAnalysis() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    public JobDescription getJobDescription() { return jobDescription; }
    public void setJobDescription(JobDescription jobDescription) { this.jobDescription = jobDescription; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
        this.scoreCategory = AtsCategory.fromScore(overallScore);
    }

    public AtsCategory getScoreCategory() { return scoreCategory; }
    public void setScoreCategory(AtsCategory scoreCategory) { this.scoreCategory = scoreCategory; }

    public AnalysisStatus getStatus() { return status; }
    public void setStatus(AnalysisStatus status) { this.status = status; }

    public Double getFormattingScore() { return formattingScore; }
    public void setFormattingScore(Double formattingScore) { this.formattingScore = formattingScore; }

    public String getFormattingIssuesJson() { return formattingIssuesJson; }
    public void setFormattingIssuesJson(String formattingIssuesJson) { this.formattingIssuesJson = formattingIssuesJson; }

    public String getStrengthsJson() { return strengthsJson; }
    public void setStrengthsJson(String strengthsJson) { this.strengthsJson = strengthsJson; }

    public String getGapsJson() { return gapsJson; }
    public void setGapsJson(String gapsJson) { this.gapsJson = gapsJson; }

    public String getSectionScoresJson() { return sectionScoresJson; }
    public void setSectionScoresJson(String sectionScoresJson) { this.sectionScoresJson = sectionScoresJson; }

    public Long getProcessingDurationMs() { return processingDurationMs; }
    public void setProcessingDurationMs(Long processingDurationMs) { this.processingDurationMs = processingDurationMs; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public ScoreBreakdown getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(ScoreBreakdown scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
        if (scoreBreakdown != null) {
            scoreBreakdown.setAnalysis(this);
        }
    }

    public java.util.Set<KeywordMatch> getKeywordMatches() { return keywordMatches; }
    public void setKeywordMatches(java.util.Set<KeywordMatch> keywordMatches) { this.keywordMatches = keywordMatches; }

    public java.util.Set<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(java.util.Set<Recommendation> recommendations) { this.recommendations = recommendations; }
}
