package com.resumematch.dto;

import com.resumematch.domain.PriorityLevel;

public class RecommendationDto {

    private Long id;
    private PriorityLevel priorityLevel;
    private String sectionTarget;
    private String title;
    private String description;
    private String antiFraudNote;
    private String originalBullet;
    private String problemIdentified;
    private String improvedBullet;
    private String whyBetter;
    private Boolean isApplied;

    public RecommendationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PriorityLevel getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(PriorityLevel priorityLevel) { this.priorityLevel = priorityLevel; }

    public String getSectionTarget() { return sectionTarget; }
    public void setSectionTarget(String sectionTarget) { this.sectionTarget = sectionTarget; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAntiFraudNote() { return antiFraudNote; }
    public void setAntiFraudNote(String antiFraudNote) { this.antiFraudNote = antiFraudNote; }

    public String getOriginalBullet() { return originalBullet; }
    public void setOriginalBullet(String originalBullet) { this.originalBullet = originalBullet; }

    public String getProblemIdentified() { return problemIdentified; }
    public void setProblemIdentified(String problemIdentified) { this.problemIdentified = problemIdentified; }

    public String getImprovedBullet() { return improvedBullet; }
    public void setImprovedBullet(String improvedBullet) { this.improvedBullet = improvedBullet; }

    public String getWhyBetter() { return whyBetter; }
    public void setWhyBetter(String whyBetter) { this.whyBetter = whyBetter; }

    public Boolean getIsApplied() { return isApplied; }
    public void setIsApplied(Boolean isApplied) { this.isApplied = isApplied; }
}
