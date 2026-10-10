package com.resumematch.ats;

import com.resumematch.ai.provider.AIProviderFactory;
import com.resumematch.ai.provider.MockAIProvider;
import com.resumematch.ats.keyword.KeywordExtractionEngine;
import com.resumematch.ats.scoring.AtsScore;
import com.resumematch.ats.scoring.DefaultAtsScoringEngine;
import com.resumematch.ats.semantic.SemanticSimilarityService;
import com.resumematch.config.AtsProperties;
import com.resumematch.domain.AtsCategory;
import com.resumematch.entity.JobDescription;
import com.resumematch.entity.Resume;
import com.resumematch.service.SampleDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AtsScoringEngineTest {

    private DefaultAtsScoringEngine scoringEngine;

    @BeforeEach
    void setUp() {
        AtsProperties props = new AtsProperties();
        KeywordExtractionEngine kwEngine = new KeywordExtractionEngine();
        SemanticSimilarityService semService = new SemanticSimilarityService();
        AIProviderFactory aiFactory = new AIProviderFactory(List.of(new MockAIProvider()));

        scoringEngine = new DefaultAtsScoringEngine(props, kwEngine, semService, aiFactory);
    }

    @Test
    @DisplayName("Should evaluate Alexander Chen resume against Senior Java JD with strong score")
    void testSampleJavaEvaluation() {
        Resume resume = new Resume();
        resume.setTitle("Alexander Chen Resume");
        resume.setRawText(SampleDataService.SAMPLE_JAVA_RESUME);

        JobDescription jd = new JobDescription();
        jd.setTitle("Senior Java Developer");
        jd.setRawText(SampleDataService.SAMPLE_JAVA_JD);

        AtsScore score = scoringEngine.analyze(resume, jd);

        assertTrue(score.getOverallScore() >= 65.0, "Score should be at least 65 for a strong baseline resume, was " + score.getOverallScore());
        assertTrue(score.getKeywordMatchScore() >= 50.0, "Keyword score was: " + score.getKeywordMatchScore());
        assertTrue(score.getFormattingCompatibilityScore() >= 85.0);
        assertFalse(score.getTopStrengths().isEmpty());
        assertFalse(score.getCriticalGaps().isEmpty());
        assertFalse(score.getRecommendations().isEmpty());
    }
}
