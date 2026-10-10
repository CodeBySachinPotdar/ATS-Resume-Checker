package com.resumematch.dto;

import java.time.Instant;

public class JobDescriptionDto {

    private Long id;
    private String title;
    private String company;
    private String location;
    private String employmentType;
    private String experienceRequirement;
    private String rawText;
    private Instant createdAt;

    public JobDescriptionDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getExperienceRequirement() { return experienceRequirement; }
    public void setExperienceRequirement(String experienceRequirement) { this.experienceRequirement = experienceRequirement; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
