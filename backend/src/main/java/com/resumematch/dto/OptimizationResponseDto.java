package com.resumematch.dto;

import java.util.List;

public class OptimizationResponseDto {

    private Long resumeId;
    private String targetJobTitle;
    private String originalSummary;
    private String improvedSummary;
    private String summaryRationale;
    private List<RecommendationDto> bulletImprovements;
    private List<RecommendationDto> generalRecommendations;

    public OptimizationResponseDto() {}

    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }

    public String getTargetJobTitle() { return targetJobTitle; }
    public void setTargetJobTitle(String targetJobTitle) { this.targetJobTitle = targetJobTitle; }

    public String getOriginalSummary() { return originalSummary; }
    public void setOriginalSummary(String originalSummary) { this.originalSummary = originalSummary; }

    public String getImprovedSummary() { return improvedSummary; }
    public void setImprovedSummary(String improvedSummary) { this.improvedSummary = improvedSummary; }

    public String getSummaryRationale() { return summaryRationale; }
    public void setSummaryRationale(String summaryRationale) { this.summaryRationale = summaryRationale; }

    public List<RecommendationDto> getBulletImprovements() { return bulletImprovements; }
    public void setBulletImprovements(List<RecommendationDto> bulletImprovements) { this.bulletImprovements = bulletImprovements; }

    public List<RecommendationDto> getGeneralRecommendations() { return generalRecommendations; }
    public void setGeneralRecommendations(List<RecommendationDto> generalRecommendations) { this.generalRecommendations = generalRecommendations; }
}
