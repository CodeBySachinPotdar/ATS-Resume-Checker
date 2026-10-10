package com.resumematch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.scoring.weights")
public class AtsProperties {

    private double keywordMatch = 0.25;
    private double requiredSkills = 0.20;
    private double experienceAlignment = 0.15;
    private double jobTitleAlignment = 0.10;
    private double semanticSimilarity = 0.10;
    private double resumeStructure = 0.10;
    private double achievementQuality = 0.05;
    private double formattingCompatibility = 0.05;

    public AtsProperties() {}

    public double getKeywordMatch() { return keywordMatch; }
    public void setKeywordMatch(double keywordMatch) { this.keywordMatch = keywordMatch; }

    public double getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(double requiredSkills) { this.requiredSkills = requiredSkills; }

    public double getExperienceAlignment() { return experienceAlignment; }
    public void setExperienceAlignment(double experienceAlignment) { this.experienceAlignment = experienceAlignment; }

    public double getJobTitleAlignment() { return jobTitleAlignment; }
    public void setJobTitleAlignment(double jobTitleAlignment) { this.jobTitleAlignment = jobTitleAlignment; }

    public double getSemanticSimilarity() { return semanticSimilarity; }
    public void setSemanticSimilarity(double semanticSimilarity) { this.semanticSimilarity = semanticSimilarity; }

    public double getResumeStructure() { return resumeStructure; }
    public void setResumeStructure(double resumeStructure) { this.resumeStructure = resumeStructure; }

    public double getAchievementQuality() { return achievementQuality; }
    public void setAchievementQuality(double achievementQuality) { this.achievementQuality = achievementQuality; }

    public double getFormattingCompatibility() { return formattingCompatibility; }
    public void setFormattingCompatibility(double formattingCompatibility) { this.formattingCompatibility = formattingCompatibility; }
}
