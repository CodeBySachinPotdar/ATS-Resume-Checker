package com.resumematch.domain;

public enum AnalysisStatus {
    PENDING,
    PARSING_RESUME,
    EXTRACTING_REQUIREMENTS,
    SCORING,
    AI_ENRICHMENT,
    COMPLETED,
    FAILED
}
