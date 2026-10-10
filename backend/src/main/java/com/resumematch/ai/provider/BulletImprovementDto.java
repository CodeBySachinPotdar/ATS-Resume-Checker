package com.resumematch.ai.provider;

public class BulletImprovementDto {
    private String original;
    private String problem;
    private String improved;
    private String whyBetter;
    private String section;

    public BulletImprovementDto() {}

    public BulletImprovementDto(String original, String problem, String improved, String whyBetter, String section) {
        this.original = original;
        this.problem = problem;
        this.improved = improved;
        this.whyBetter = whyBetter;
        this.section = section;
    }

    public String getOriginal() { return original; }
    public void setOriginal(String original) { this.original = original; }

    public String getProblem() { return problem; }
    public void setProblem(String problem) { this.problem = problem; }

    public String getImproved() { return improved; }
    public void setImproved(String improved) { this.improved = improved; }

    public String getWhyBetter() { return whyBetter; }
    public void setWhyBetter(String whyBetter) { this.whyBetter = whyBetter; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
}
