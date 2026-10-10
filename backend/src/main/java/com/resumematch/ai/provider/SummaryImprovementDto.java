package com.resumematch.ai.provider;

public class SummaryImprovementDto {
    private String originalSummary;
    private String improvedSummary;
    private String rationale;
    private String targetJobTitle;

    public SummaryImprovementDto() {}

    public SummaryImprovementDto(String originalSummary, String improvedSummary, String rationale, String targetJobTitle) {
        this.originalSummary = originalSummary;
        this.improvedSummary = improvedSummary;
        this.rationale = rationale;
        this.targetJobTitle = targetJobTitle;
    }

    public String getOriginalSummary() { return originalSummary; }
    public void setOriginalSummary(String originalSummary) { this.originalSummary = originalSummary; }

    public String getImprovedSummary() { return improvedSummary; }
    public void setImprovedSummary(String improvedSummary) { this.improvedSummary = improvedSummary; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public String getTargetJobTitle() { return targetJobTitle; }
    public void setTargetJobTitle(String targetJobTitle) { this.targetJobTitle = targetJobTitle; }
}
