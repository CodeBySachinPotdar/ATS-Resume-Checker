package com.resumematch.entity;

import com.resumematch.domain.MatchStatus;
import com.resumematch.domain.PriorityLevel;
import com.resumematch.domain.SkillCategory;
import jakarta.persistence.*;

@Entity
@Table(name = "keyword_matches")
public class KeywordMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private ResumeAnalysis analysis;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_status", nullable = false, length = 20)
    private MatchStatus matchStatus; // MATCHED, RELATED, MISSING

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_category", nullable = false, length = 30)
    private SkillCategory skillCategory = SkillCategory.TECHNICAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority_level", nullable = false, length = 20)
    private PriorityLevel priorityLevel = PriorityLevel.MEDIUM;

    @Column(name = "related_term", length = 100)
    private String relatedTerm; // e.g. "REST APIs" for "RESTful Web Services"

    @Column(name = "importance_explanation", length = 500)
    private String importanceExplanation;

    @Column(name = "is_must_have")
    private Boolean isMustHave = false;

    public KeywordMatch() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ResumeAnalysis getAnalysis() { return analysis; }
    public void setAnalysis(ResumeAnalysis analysis) { this.analysis = analysis; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public MatchStatus getMatchStatus() { return matchStatus; }
    public void setMatchStatus(MatchStatus matchStatus) { this.matchStatus = matchStatus; }

    public SkillCategory getSkillCategory() { return skillCategory; }
    public void setSkillCategory(SkillCategory skillCategory) { this.skillCategory = skillCategory; }

    public PriorityLevel getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(PriorityLevel priorityLevel) { this.priorityLevel = priorityLevel; }

    public String getRelatedTerm() { return relatedTerm; }
    public void setRelatedTerm(String relatedTerm) { this.relatedTerm = relatedTerm; }

    public String getImportanceExplanation() { return importanceExplanation; }
    public void setImportanceExplanation(String importanceExplanation) { this.importanceExplanation = importanceExplanation; }

    public Boolean getIsMustHave() { return isMustHave; }
    public void setIsMustHave(Boolean isMustHave) { this.isMustHave = isMustHave; }
}
