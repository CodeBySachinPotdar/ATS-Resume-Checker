package com.resumematch.domain;

public enum TemplateType {
    CLASSIC_PROFESSIONAL("Classic Professional", "Timeless, clean single-column format optimized for all ATS parsers."),
    MODERN_MINIMAL("Modern Minimal", "Contemporary clean typography with balanced whitespace and clear hierarchy."),
    SOFTWARE_ENGINEER("Software Engineer", "Specialized layout highlighting technical stacks, repositories, and measurable impact."),
    EXECUTIVE("Executive", "Formal layout emphasizing leadership, strategic accomplishments, and executive summaries.");

    private final String displayName;
    private final String description;

    TemplateType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
