package com.resumematch.parser.model;

import java.util.ArrayList;
import java.util.List;

public class FormattingInspection {
    private double score = 100.0;
    private boolean hasTables = false;
    private boolean hasMultipleColumns = false;
    private boolean hasTextBoxes = false;
    private boolean hasImages = false;
    private boolean hasUnusualFonts = false;
    private boolean hasCriticalHeaderFooter = false;
    private boolean hasStandardHeadings = true;
    private boolean textIsMachineReadable = true;
    private List<String> passedChecks = new ArrayList<>();
    private List<String> warningIssues = new ArrayList<>();
    private List<String> criticalIssues = new ArrayList<>();

    public FormattingInspection() {}

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public boolean isHasTables() { return hasTables; }
    public void setHasTables(boolean hasTables) { this.hasTables = hasTables; }

    public boolean isHasMultipleColumns() { return hasMultipleColumns; }
    public void setHasMultipleColumns(boolean hasMultipleColumns) { this.hasMultipleColumns = hasMultipleColumns; }

    public boolean isHasTextBoxes() { return hasTextBoxes; }
    public void setHasTextBoxes(boolean hasTextBoxes) { this.hasTextBoxes = hasTextBoxes; }

    public boolean isHasImages() { return hasImages; }
    public void setHasImages(boolean hasImages) { this.hasImages = hasImages; }

    public boolean isHasUnusualFonts() { return hasUnusualFonts; }
    public void setHasUnusualFonts(boolean hasUnusualFonts) { this.hasUnusualFonts = hasUnusualFonts; }

    public boolean isHasCriticalHeaderFooter() { return hasCriticalHeaderFooter; }
    public void setHasCriticalHeaderFooter(boolean hasCriticalHeaderFooter) { this.hasCriticalHeaderFooter = hasCriticalHeaderFooter; }

    public boolean isHasStandardHeadings() { return hasStandardHeadings; }
    public void setHasStandardHeadings(boolean hasStandardHeadings) { this.hasStandardHeadings = hasStandardHeadings; }

    public boolean isTextIsMachineReadable() { return textIsMachineReadable; }
    public void setTextIsMachineReadable(boolean textIsMachineReadable) { this.textIsMachineReadable = textIsMachineReadable; }

    public List<String> getPassedChecks() { return passedChecks; }
    public void setPassedChecks(List<String> passedChecks) { this.passedChecks = passedChecks; }

    public List<String> getWarningIssues() { return warningIssues; }
    public void setWarningIssues(List<String> warningIssues) { this.warningIssues = warningIssues; }

    public List<String> getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(List<String> criticalIssues) { this.criticalIssues = criticalIssues; }
}
