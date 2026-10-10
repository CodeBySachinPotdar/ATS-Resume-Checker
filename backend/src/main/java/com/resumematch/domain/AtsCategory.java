package com.resumematch.domain;

public enum AtsCategory {
    EXCELLENT_MATCH("Excellent Match", 90, 100, "Your resume aligns exceptionally well with the target role requirements."),
    STRONG_MATCH("Strong Match", 80, 89, "Your resume demonstrates strong competency and substantial alignment with the core requirements."),
    GOOD_MATCH("Good Match", 70, 79, "Your resume matches key requirements but has noticeable gaps in high-priority skills or metrics."),
    NEEDS_IMPROVEMENT("Needs Improvement", 60, 69, "Significant gaps detected in required technologies, keyword frequency, or achievement formatting."),
    POOR_MATCH("Poor Match", 0, 59, "Major misalignment with job prerequisites, missing core domain skills, or problematic document structure.");

    private final String label;
    private final int minScore;
    private final int maxScore;
    private final String description;

    AtsCategory(String label, int minScore, int maxScore, String description) {
        this.label = label;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.description = description;
    }

    public static AtsCategory fromScore(double score) {
        int rounded = (int) Math.round(score);
        if (rounded >= 90) return EXCELLENT_MATCH;
        if (rounded >= 80) return STRONG_MATCH;
        if (rounded >= 70) return GOOD_MATCH;
        if (rounded >= 60) return NEEDS_IMPROVEMENT;
        return POOR_MATCH;
    }

    public String getLabel() { return label; }
    public int getMinScore() { return minScore; }
    public int getMaxScore() { return maxScore; }
    public String getDescription() { return description; }
}
