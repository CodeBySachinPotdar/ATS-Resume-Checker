package com.resumematch.dto;

import com.resumematch.ats.scoring.CategoryBreakdownDetail;
import java.util.Map;

public class ScoreBreakdownDto {

    private double keywordMatchScore;
    private double requiredSkillsScore;
    private double experienceAlignmentScore;
    private double jobTitleAlignmentScore;
    private double semanticSimilarityScore;
    private double resumeStructureScore;
    private double achievementQualityScore;
    private double formattingCompatibilityScore;
    private Map<String, CategoryBreakdownDetail> breakdownDetails;

    public ScoreBreakdownDto() {}

    public double getKeywordMatchScore() { return keywordMatchScore; }
    public void setKeywordMatchScore(double keywordMatchScore) { this.keywordMatchScore = keywordMatchScore; }

    public double getRequiredSkillsScore() { return requiredSkillsScore; }
    public void setRequiredSkillsScore(double requiredSkillsScore) { this.requiredSkillsScore = requiredSkillsScore; }

    public double getExperienceAlignmentScore() { return experienceAlignmentScore; }
    public void setExperienceAlignmentScore(double experienceAlignmentScore) { this.experienceAlignmentScore = experienceAlignmentScore; }

    public double getJobTitleAlignmentScore() { return jobTitleAlignmentScore; }
    public void setJobTitleAlignmentScore(double jobTitleAlignmentScore) { this.jobTitleAlignmentScore = jobTitleAlignmentScore; }

    public double getSemanticSimilarityScore() { return semanticSimilarityScore; }
    public void setSemanticSimilarityScore(double semanticSimilarityScore) { this.semanticSimilarityScore = semanticSimilarityScore; }

    public double getResumeStructureScore() { return resumeStructureScore; }
    public void setResumeStructureScore(double resumeStructureScore) { this.resumeStructureScore = resumeStructureScore; }

    public double getAchievementQualityScore() { return achievementQualityScore; }
    public void setAchievementQualityScore(double achievementQualityScore) { this.achievementQualityScore = achievementQualityScore; }

    public double getFormattingCompatibilityScore() { return formattingCompatibilityScore; }
    public void setFormattingCompatibilityScore(double formattingCompatibilityScore) { this.formattingCompatibilityScore = formattingCompatibilityScore; }

    public Map<String, CategoryBreakdownDetail> getBreakdownDetails() { return breakdownDetails; }
    public void setBreakdownDetails(Map<String, CategoryBreakdownDetail> breakdownDetails) { this.breakdownDetails = breakdownDetails; }
}
