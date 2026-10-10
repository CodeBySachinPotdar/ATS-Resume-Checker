package com.resumematch.entity;

import com.resumematch.domain.PriorityLevel;
import jakarta.persistence.*;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private ResumeAnalysis analysis;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false, length = 20)
    private PriorityLevel priorityLevel = PriorityLevel.MEDIUM;

    @Column(name = "section_target", length = 50)
    private String sectionTarget; // SUMMARY, EXPERIENCE, SKILLS, EDUCATION, FORMATTING

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(name = "anti_fraud_note", columnDefinition = "TEXT")
    private String antiFraudNote; // E.g. "Only add if you have genuine practical experience"

    // Optional bullet refinement fields
    @Lob
    @Column(name = "original_bullet", columnDefinition = "TEXT")
    private String originalBullet;

    @Lob
    @Column(name = "problem_identified", columnDefinition = "TEXT")
    private String problemIdentified;

    @Lob
    @Column(name = "improved_bullet", columnDefinition = "TEXT")
    private String improvedBullet;

    @Lob
    @Column(name = "why_better", columnDefinition = "TEXT")
    private String whyBetter;

    @Column(name = "is_applied")
    private Boolean isApplied = false;

    public Recommendation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ResumeAnalysis getAnalysis() { return analysis; }
    public void setAnalysis(ResumeAnalysis analysis) { this.analysis = analysis; }

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
