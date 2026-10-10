package com.resumematch.dto;

import com.resumematch.domain.AnalysisStatus;
import com.resumematch.domain.AtsCategory;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class AnalysisResponseDto {

    private Long id;
    private Long resumeId;
    private String resumeTitle;
    private Long jobDescriptionId;
    private String jobTitle;
    private String company;
    private Double overallScore;
    private AtsCategory scoreCategory;
    private AnalysisStatus status;
    private Double formattingScore;
    private Long processingDurationMs;
    private Instant createdAt;

    private ScoreBreakdownDto scoreBreakdown;
    private List<KeywordMatchDto> keywordMatches;
    private List<RecommendationDto> recommendations;
    private List<String> topStrengths;
    private List<String> criticalGaps;
    private Map<String, Double> sectionScores;

    public AnalysisResponseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getResumeTitle() { return resumeTitle; }
    public void setResumeTitle(String resumeTitle) { this.resumeTitle = resumeTitle; }

    public Long getJobDescriptionId() { return jobDescriptionId; }
    public void setJobDescriptionId(Long jobDescriptionId) { this.jobDescriptionId = jobDescriptionId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }

    public AtsCategory getScoreCategory() { return scoreCategory; }
    public void setScoreCategory(AtsCategory scoreCategory) { this.scoreCategory = scoreCategory; }

    public AnalysisStatus getStatus() { return status; }
    public void setStatus(AnalysisStatus status) { this.status = status; }

    public Double getFormattingScore() { return formattingScore; }
    public void setFormattingScore(Double formattingScore) { this.formattingScore = formattingScore; }

    public Long getProcessingDurationMs() { return processingDurationMs; }
    public void setProcessingDurationMs(Long processingDurationMs) { this.processingDurationMs = processingDurationMs; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public ScoreBreakdownDto getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(ScoreBreakdownDto scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }

    public List<KeywordMatchDto> getKeywordMatches() { return keywordMatches; }
    public void setKeywordMatches(List<KeywordMatchDto> keywordMatches) { this.keywordMatches = keywordMatches; }

    public List<RecommendationDto> getRecommendations() { return recommendations; }
    public void setRecommendations(List<RecommendationDto> recommendations) { this.recommendations = recommendations; }

    public List<String> getTopStrengths() { return topStrengths; }
    public void setTopStrengths(List<String> topStrengths) { this.topStrengths = topStrengths; }

    public List<String> getCriticalGaps() { return criticalGaps; }
    public void setCriticalGaps(List<String> criticalGaps) { this.criticalGaps = criticalGaps; }

    public Map<String, Double> getSectionScores() { return sectionScores; }
    public void setSectionScores(Map<String, Double> sectionScores) { this.sectionScores = sectionScores; }
}
