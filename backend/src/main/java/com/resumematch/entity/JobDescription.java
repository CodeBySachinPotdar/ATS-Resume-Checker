package com.resumematch.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_descriptions")
public class JobDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 150)
    private String company;

    @Column(length = 100)
    private String location;

    @Column(name = "employment_type", length = 50)
    private String employmentType; // Full-time, Contract, etc.

    @Column(name = "experience_requirement", length = 100)
    private String experienceRequirement; // e.g. "5+ years"

    @Lob
    @Column(name = "raw_text", nullable = false, columnDefinition = "TEXT")
    private String rawText;

    @Lob
    @Column(name = "extracted_json", columnDefinition = "TEXT")
    private String extractedJson; // Structured requirements

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "jobDescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeAnalysis> analyses = new ArrayList<>();

    public JobDescription() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public String getExtractedJson() { return extractedJson; }
    public void setExtractedJson(String extractedJson) { this.extractedJson = extractedJson; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public List<ResumeAnalysis> getAnalyses() { return analyses; }
    public void setAnalyses(List<ResumeAnalysis> analyses) { this.analyses = analyses; }
}
