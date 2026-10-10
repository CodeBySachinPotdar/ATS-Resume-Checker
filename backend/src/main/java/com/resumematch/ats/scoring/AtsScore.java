package com.resumematch.ats.scoring;

import com.resumematch.domain.AtsCategory;
import com.resumematch.entity.KeywordMatch;
import com.resumematch.entity.Recommendation;

import java.util.*;

public class AtsScore {

    private double overallScore;
    private AtsCategory category;
    private double keywordMatchScore;
    private double requiredSkillsScore;
    private double experienceAlignmentScore;
    private double jobTitleAlignmentScore;
    private double semanticSimilarityScore;
    private double resumeStructureScore;
    private double achievementQualityScore;
    private double formattingCompatibilityScore;

    private Map<String, CategoryBreakdownDetail> breakdownDetails = new LinkedHashMap<>();
    private List<KeywordMatch> keywordMatches = new ArrayList<>();
    private List<Recommendation> recommendations = new ArrayList<>();
    private List<String> topStrengths = new ArrayList<>();
    private List<String> criticalGaps = new ArrayList<>();
    private Map<String, Double> sectionScores = new LinkedHashMap<>();

    public AtsScore() {}

    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) {
        this.overallScore = Math.round(overallScore * 10.0) / 10.0;
        this.category = AtsCategory.fromScore(this.overallScore);
    }

    public AtsCategory getCategory() { return category; }
    public void setCategory(AtsCategory category) { this.category = category; }

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

    public List<KeywordMatch> getKeywordMatches() { return keywordMatches; }
    public void setKeywordMatches(List<KeywordMatch> keywordMatches) { this.keywordMatches = keywordMatches; }

    public List<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }

    public List<String> getTopStrengths() { return topStrengths; }
    public void setTopStrengths(List<String> topStrengths) { this.topStrengths = topStrengths; }

    public List<String> getCriticalGaps() { return criticalGaps; }
    public void setCriticalGaps(List<String> criticalGaps) { this.criticalGaps = criticalGaps; }

    public Map<String, Double> getSectionScores() { return sectionScores; }
    public void setSectionScores(Map<String, Double> sectionScores) { this.sectionScores = sectionScores; }
}
