package com.resumematch.ats.keyword;

import com.resumematch.domain.MatchStatus;
import com.resumematch.entity.KeywordMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeywordExtractionEngineTest {

    private KeywordExtractionEngine keywordEngine;

    @BeforeEach
    void setUp() {
        keywordEngine = new KeywordExtractionEngine();
    }

    @Test
    @DisplayName("Should detect direct matched keywords between resume and JD")
    void testDirectMatchKeywords() {
        String resume = "Experienced in Java, Spring Boot, Microservices, and PostgreSQL database.";
        String jd = "Looking for Senior Engineer with Java, Spring Boot, and PostgreSQL.";

        List<KeywordMatch> matches = keywordEngine.extractAndMatch(resume, jd);

        assertTrue(matches.stream().anyMatch(m -> m.getKeyword().equals("Java") && m.getMatchStatus() == MatchStatus.MATCHED));
        assertTrue(matches.stream().anyMatch(m -> m.getKeyword().equals("Spring Boot") && m.getMatchStatus() == MatchStatus.MATCHED));
        assertTrue(matches.stream().anyMatch(m -> m.getKeyword().equals("PostgreSQL") && m.getMatchStatus() == MatchStatus.MATCHED));
    }

    @Test
    @DisplayName("Should detect semantic synonym relationships such as REST APIs to RESTful Web Services")
    void testSemanticSynonymMatch() {
        String resume = "Designed high performance REST APIs and distributed backend services.";
        String jd = "Requirements: Strong expertise in RESTful Web Services and Kafka.";

        List<KeywordMatch> matches = keywordEngine.extractAndMatch(resume, jd);

        assertTrue(matches.stream().anyMatch(m -> m.getKeyword().equals("RESTful Web Services")
                && m.getMatchStatus() == MatchStatus.RELATED
                && m.getRelatedTerm().equals("REST APIs")));
    }

    @Test
    @DisplayName("Should flag missing technologies present in JD but absent from resume")
    void testMissingKeywords() {
        String resume = "Java developer specializing in Spring Boot.";
        String jd = "Must have: Java, Spring Boot, Kubernetes, and AWS.";

        List<KeywordMatch> matches = keywordEngine.extractAndMatch(resume, jd);

        assertTrue(matches.stream().anyMatch(m -> m.getKeyword().equals("Kubernetes") && m.getMatchStatus() == MatchStatus.MISSING));
        assertTrue(matches.stream().anyMatch(m -> m.getKeyword().equals("AWS") && m.getMatchStatus() == MatchStatus.MISSING));
    }
}
