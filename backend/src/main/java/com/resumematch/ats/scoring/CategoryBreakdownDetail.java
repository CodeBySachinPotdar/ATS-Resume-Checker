package com.resumematch.ats.scoring;

import java.util.ArrayList;
import java.util.List;

public class CategoryBreakdownDetail {
    private String categoryKey; // KEYWORD_MATCH, REQUIRED_SKILLS, etc.
    private String title;
    private double score;
    private double weight;
    private double weightedContribution;
    private String whyAssigned;
    private List<String> whatDetected = new ArrayList<>();
    private List<String> whatMissing = new ArrayList<>();
    private List<String> howToImprove = new ArrayList<>();

    public CategoryBreakdownDetail() {}

    public CategoryBreakdownDetail(String categoryKey, String title, double score, double weight, String whyAssigned) {
        this.categoryKey = categoryKey;
        this.title = title;
        this.score = Math.round(score * 10.0) / 10.0;
        this.weight = weight;
        this.weightedContribution = Math.round((this.score * weight) * 10.0) / 10.0;
        this.whyAssigned = whyAssigned;
    }

    public String getCategoryKey() { return categoryKey; }
    public void setCategoryKey(String categoryKey) { this.categoryKey = categoryKey; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getWeightedContribution() { return weightedContribution; }
    public void setWeightedContribution(double weightedContribution) { this.weightedContribution = weightedContribution; }

    public String getWhyAssigned() { return whyAssigned; }
    public void setWhyAssigned(String whyAssigned) { this.whyAssigned = whyAssigned; }

    public List<String> getWhatDetected() { return whatDetected; }
    public void setWhatDetected(List<String> whatDetected) { this.whatDetected = whatDetected; }

    public List<String> getWhatMissing() { return whatMissing; }
    public void setWhatMissing(List<String> whatMissing) { this.whatMissing = whatMissing; }

    public List<String> getHowToImprove() { return howToImprove; }
    public void setHowToImprove(List<String> howToImprove) { this.howToImprove = howToImprove; }
}
