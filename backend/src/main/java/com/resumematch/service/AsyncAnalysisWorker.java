package com.resumematch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumematch.ats.AtsScoringEngine;
import com.resumematch.ats.scoring.AtsScore;
import com.resumematch.domain.AnalysisStatus;
import com.resumematch.entity.*;
import com.resumematch.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsyncAnalysisWorker {

    private static final Logger log = LoggerFactory.getLogger(AsyncAnalysisWorker.class);

    private final ResumeAnalysisRepository analysisRepository;
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final AnalysisHistoryRepository historyRepository;
    private final AtsScoringEngine scoringEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AsyncAnalysisWorker(
            ResumeAnalysisRepository analysisRepository,
            ResumeRepository resumeRepository,
            JobDescriptionRepository jobDescriptionRepository,
            AnalysisHistoryRepository historyRepository,
            AtsScoringEngine scoringEngine
    ) {
        this.analysisRepository = analysisRepository;
        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.historyRepository = historyRepository;
        this.scoringEngine = scoringEngine;
    }

    @Async
    @Transactional
    public void processJob(Long analysisId, Long resumeId, Long jobDescriptionId) {
        long startTime = System.currentTimeMillis();
        log.info("Starting ATS analysis for analysisId={}", analysisId);

        ResumeAnalysis analysis = analysisRepository.findById(analysisId).orElse(null);
        if (analysis == null) {
            log.error("Analysis record not found for id={}", analysisId);
            return;
        }

        Resume resume = resumeRepository.findById(resumeId).orElse(null);
        JobDescription jd = jobDescriptionRepository.findById(jobDescriptionId).orElse(null);

        if (resume == null || jd == null) {
            analysis.setStatus(AnalysisStatus.FAILED);
            analysisRepository.save(analysis);
            return;
        }

        try {
            // Stage 1: Parsing
            analysis.setStatus(AnalysisStatus.PARSING_RESUME);
            analysisRepository.save(analysis);

            // Stage 2: Extracting Requirements
            analysis.setStatus(AnalysisStatus.EXTRACTING_REQUIREMENTS);
            analysisRepository.save(analysis);

            // Stage 3: Scoring & Stage 4: AI Enrichment
            analysis.setStatus(AnalysisStatus.SCORING);
            analysisRepository.save(analysis);

            AtsScore calculated = scoringEngine.analyze(resume, jd);

            // Map calculated score to analysis entity
            analysis.setOverallScore(calculated.getOverallScore());
            analysis.setScoreCategory(calculated.getCategory());
            analysis.setFormattingScore(calculated.getFormattingCompatibilityScore());

            // Score Breakdown
            ScoreBreakdown breakdown = new ScoreBreakdown();
            breakdown.setAnalysis(analysis);
            breakdown.setKeywordMatchScore(calculated.getKeywordMatchScore());
            breakdown.setRequiredSkillsScore(calculated.getRequiredSkillsScore());
            breakdown.setExperienceAlignmentScore(calculated.getExperienceAlignmentScore());
            breakdown.setJobTitleAlignmentScore(calculated.getJobTitleAlignmentScore());
            breakdown.setSemanticSimilarityScore(calculated.getSemanticSimilarityScore());
            breakdown.setResumeStructureScore(calculated.getResumeStructureScore());
            breakdown.setAchievementQualityScore(calculated.getAchievementQualityScore());
            breakdown.setFormattingCompatibilityScore(calculated.getFormattingCompatibilityScore());
            breakdown.setBreakdownDetailsJson(objectMapper.writeValueAsString(calculated.getBreakdownDetails()));
            analysis.setScoreBreakdown(breakdown);

            // Keyword Matches
            analysis.getKeywordMatches().clear();
            for (KeywordMatch km : calculated.getKeywordMatches()) {
                km.setAnalysis(analysis);
                analysis.getKeywordMatches().add(km);
            }

            // Recommendations
            analysis.getRecommendations().clear();
            for (Recommendation rec : calculated.getRecommendations()) {
                rec.setAnalysis(analysis);
                analysis.getRecommendations().add(rec);
            }

            // Metadata JSONs
            analysis.setStrengthsJson(objectMapper.writeValueAsString(calculated.getTopStrengths()));
            analysis.setGapsJson(objectMapper.writeValueAsString(calculated.getCriticalGaps()));
            analysis.setSectionScoresJson(objectMapper.writeValueAsString(calculated.getSectionScores()));

            analysis.setProcessingDurationMs(System.currentTimeMillis() - startTime);
            analysis.setStatus(AnalysisStatus.COMPLETED);
            analysisRepository.save(analysis);

            // Record History
            Long userId = resume.getUser() != null ? resume.getUser().getId() : null;
            historyRepository.save(new AnalysisHistory(
                    userId,
                    analysis.getId(),
                    resume.getTitle(),
                    jd.getTitle(),
                    jd.getCompany(),
                    analysis.getOverallScore()
            ));

            log.info("Completed ATS analysis id={} with score={}", analysisId, analysis.getOverallScore());

        } catch (Exception e) {
            log.error("Failed to complete ATS analysis id={}: {}", analysisId, e.getMessage(), e);
            analysis.setStatus(AnalysisStatus.FAILED);
            analysisRepository.save(analysis);
        }
    }
}
