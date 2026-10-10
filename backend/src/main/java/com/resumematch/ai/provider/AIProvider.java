package com.resumematch.ai.provider;

import java.util.List;

public interface AIProvider {

    String getProviderName();

    List<BulletImprovementDto> analyzeAndImproveBullets(
            List<String> bullets,
            String targetRole,
            List<String> targetKeywords
    );

    SummaryImprovementDto improveSummary(
            String currentSummary,
            String targetRole,
            List<String> keySkills
    );

    List<AiRecommendationDto> generateRecommendations(
            String resumeText,
            String jdText,
            List<String> missingKeywords
    );

    String extractJobRequirementsJson(String jdText);

    String extractResumeStructureJson(String resumeText);
}
