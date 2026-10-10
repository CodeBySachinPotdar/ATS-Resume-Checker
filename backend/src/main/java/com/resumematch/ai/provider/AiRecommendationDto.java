package com.resumematch.ai.provider;

import com.resumematch.domain.PriorityLevel;

public class AiRecommendationDto {
    private PriorityLevel priority;
    private String category;
    private String title;
    private String description;
    private String antiFraudNote;

    public AiRecommendationDto() {}

    public AiRecommendationDto(PriorityLevel priority, String category, String title, String description, String antiFraudNote) {
        this.priority = priority;
        this.category = category;
        this.title = title;
        this.description = description;
        this.antiFraudNote = antiFraudNote;
    }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAntiFraudNote() { return antiFraudNote; }
    public void setAntiFraudNote(String antiFraudNote) { this.antiFraudNote = antiFraudNote; }
}
