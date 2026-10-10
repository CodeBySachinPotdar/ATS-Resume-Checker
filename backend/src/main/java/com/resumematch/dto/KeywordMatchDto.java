package com.resumematch.dto;

import com.resumematch.domain.MatchStatus;
import com.resumematch.domain.PriorityLevel;
import com.resumematch.domain.SkillCategory;

public class KeywordMatchDto {

    private Long id;
    private String keyword;
    private MatchStatus matchStatus;
    private SkillCategory skillCategory;
    private PriorityLevel priorityLevel;
    private String relatedTerm;
    private String importanceExplanation;
    private Boolean isMustHave;

    public KeywordMatchDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
