package com.resumematch.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "score_breakdowns")
public class ScoreBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private ResumeAnalysis analysis;

    @Column(name = "keyword_match_score", nullable = false)
    private Double keywordMatchScore = 0.0; // 0-100

    @Column(name = "required_skills_score", nullable = false)
    private Double requiredSkillsScore = 0.0;

    @Column(name = "experience_alignment_score", nullable = false)
    private Double experienceAlignmentScore = 0.0;

    @Column(name = "job_title_alignment_score", nullable = false)
    private Double jobTitleAlignmentScore = 0.0;

    @Column(name = "semantic_similarity_score", nullable = false)
    private Double semanticSimilarityScore = 0.0;

    @Column(name = "resume_structure_score", nullable = false)
    private Double resumeStructureScore = 0.0;

    @Column(name = "achievement_quality_score", nullable = false)
    private Double achievementQualityScore = 0.0;

    @Column(name = "formatting_compatibility_score", nullable = false)
    private Double formattingCompatibilityScore = 0.0;

    // Detailed explanation JSON strings for why/what detected/what missing/how to improve
    @Lob
    @Column(name = "breakdown_details_json", columnDefinition = "TEXT")
    private String breakdownDetailsJson;

    public ScoreBreakdown() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ResumeAnalysis getAnalysis() { return analysis; }
    public void setAnalysis(ResumeAnalysis analysis) { this.analysis = analysis; }

    public Double getKeywordMatchScore() { return keywordMatchScore; }
    public void setKeywordMatchScore(Double keywordMatchScore) { this.keywordMatchScore = keywordMatchScore; }

    public Double getRequiredSkillsScore() { return requiredSkillsScore; }
    public void setRequiredSkillsScore(Double requiredSkillsScore) { this.requiredSkillsScore = requiredSkillsScore; }

    public Double getExperienceAlignmentScore() { return experienceAlignmentScore; }
    public void setExperienceAlignmentScore(Double experienceAlignmentScore) { this.experienceAlignmentScore = experienceAlignmentScore; }

    public Double getJobTitleAlignmentScore() { return jobTitleAlignmentScore; }
    public void setJobTitleAlignmentScore(Double jobTitleAlignmentScore) { this.jobTitleAlignmentScore = jobTitleAlignmentScore; }

    public Double getSemanticSimilarityScore() { return semanticSimilarityScore; }
    public void setSemanticSimilarityScore(Double semanticSimilarityScore) { this.semanticSimilarityScore = semanticSimilarityScore; }

    public Double getResumeStructureScore() { return resumeStructureScore; }
    public void setResumeStructureScore(Double resumeStructureScore) { this.resumeStructureScore = resumeStructureScore; }

    public Double getAchievementQualityScore() { return achievementQualityScore; }
    public void setAchievementQualityScore(Double achievementQualityScore) { this.achievementQualityScore = achievementQualityScore; }

    public Double getFormattingCompatibilityScore() { return formattingCompatibilityScore; }
    public void setFormattingCompatibilityScore(Double formattingCompatibilityScore) { this.formattingCompatibilityScore = formattingCompatibilityScore; }

    public String getBreakdownDetailsJson() { return breakdownDetailsJson; }
    public void setBreakdownDetailsJson(String breakdownDetailsJson) { this.breakdownDetailsJson = breakdownDetailsJson; }
}
