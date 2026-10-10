package com.resumematch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumematch.ats.AtsScoringEngine;
import com.resumematch.ats.scoring.AtsScore;
import com.resumematch.ats.scoring.CategoryBreakdownDetail;
import com.resumematch.domain.AnalysisStatus;
import com.resumematch.dto.*;
import com.resumematch.entity.*;
import com.resumematch.exception.InvalidRequestException;
import com.resumematch.exception.ResourceNotFoundException;
import com.resumematch.kafka.AnalysisEventPublisher;
import com.resumematch.kafka.AnalysisJobEvent;
import com.resumematch.repository.JobDescriptionRepository;
import com.resumematch.repository.ResumeAnalysisRepository;
import com.resumematch.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AnalysisService {

    private final ResumeAnalysisRepository analysisRepository;
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final AnalysisEventPublisher eventPublisher;
    private final AtsScoringEngine scoringEngine;
    private final AsyncAnalysisWorker asyncWorker;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisService(
            ResumeAnalysisRepository analysisRepository,
            ResumeRepository resumeRepository,
            JobDescriptionRepository jobDescriptionRepository,
            AnalysisEventPublisher eventPublisher,
            AtsScoringEngine scoringEngine,
            AsyncAnalysisWorker asyncWorker
    ) {
        this.analysisRepository = analysisRepository;
        this.resumeRepository = resumeRepository;
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.eventPublisher = eventPublisher;
        this.scoringEngine = scoringEngine;
        this.asyncWorker = asyncWorker;
    }

    @Transactional
    public AnalysisResponseDto createAnalysis(AnalysisRequest request, Long userId) {
        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + request.getResumeId()));

        JobDescription jd = jobDescriptionRepository.findById(request.getJobDescriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Job description not found with ID: " + request.getJobDescriptionId()));

        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setResume(resume);
        analysis.setJobDescription(jd);
        analysis.setStatus(AnalysisStatus.PENDING);

        ResumeAnalysis saved = analysisRepository.save(analysis);

        // Publish event for async worker (Kafka or in-process)
        AnalysisJobEvent event = new AnalysisJobEvent(saved.getId(), resume.getId(), jd.getId(), userId);
        eventPublisher.publishAnalysisJob(event);

        return mapToDto(saved);
    }

    @Transactional
    public AnalysisResponseDto runQuickAnalysis(QuickAnalysisRequest request, Long userId) {
        if (request.getResumeText() == null || request.getResumeText().trim().length() < 30) {
            throw new InvalidRequestException("Resume text is required and must contain meaningful content.");
        }
        if (request.getJobDescriptionText() == null || request.getJobDescriptionText().trim().length() < 30) {
            throw new InvalidRequestException("Job description is required and must contain meaningful content.");
        }

        // Save resume
        Resume resume = new Resume();
        resume.setTitle(request.getResumeTitle() != null ? request.getResumeTitle() : "Quick Resume");
        resume.setFileType("RAW_TEXT");
        resume.setRawText(request.getResumeText());
        Resume savedResume = resumeRepository.save(resume);

        // Save JD
        JobDescription jd = new JobDescription();
        jd.setTitle(request.getJobTitle() != null ? request.getJobTitle() : "Target Role");
        jd.setCompany(request.getCompany() != null ? request.getCompany() : "Target Company");
        jd.setRawText(request.getJobDescriptionText());
        JobDescription savedJd = jobDescriptionRepository.save(jd);

        long startTime = System.currentTimeMillis();
        AtsScore calculated = scoringEngine.analyze(savedResume, savedJd);

        // Create and populate Analysis record
        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setResume(savedResume);
        analysis.setJobDescription(savedJd);
        analysis.setOverallScore(calculated.getOverallScore());
        analysis.setScoreCategory(calculated.getCategory());
        analysis.setFormattingScore(calculated.getFormattingCompatibilityScore());
        analysis.setStatus(AnalysisStatus.COMPLETED);
        analysis.setProcessingDurationMs(System.currentTimeMillis() - startTime);

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

        try {
            breakdown.setBreakdownDetailsJson(objectMapper.writeValueAsString(calculated.getBreakdownDetails()));
            analysis.setStrengthsJson(objectMapper.writeValueAsString(calculated.getTopStrengths()));
            analysis.setGapsJson(objectMapper.writeValueAsString(calculated.getCriticalGaps()));
            analysis.setSectionScoresJson(objectMapper.writeValueAsString(calculated.getSectionScores()));
        } catch (Exception ignored) {}
        analysis.setScoreBreakdown(breakdown);

        analysis.getKeywordMatches().clear();
        for (KeywordMatch km : calculated.getKeywordMatches()) {
            km.setAnalysis(analysis);
            analysis.getKeywordMatches().add(km);
        }

        analysis.getRecommendations().clear();
        for (Recommendation rec : calculated.getRecommendations()) {
            rec.setAnalysis(analysis);
            analysis.getRecommendations().add(rec);
        }

        ResumeAnalysis savedAnalysis = analysisRepository.save(analysis);
        return mapToDto(savedAnalysis);
    }

    @Transactional(readOnly = true)
    public AnalysisResponseDto getAnalysis(Long id) {
        ResumeAnalysis analysis = analysisRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found with ID: " + id));
        return mapToDto(analysis);
    }

    @Transactional(readOnly = true)
    public ScoreBreakdownDto getScoreBreakdown(Long id) {
        ResumeAnalysis analysis = analysisRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found with ID: " + id));
        return mapScoreBreakdown(analysis.getScoreBreakdown());
    }

    @Transactional(readOnly = true)
    public List<KeywordMatchDto> getKeywords(Long id) {
        ResumeAnalysis analysis = analysisRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found with ID: " + id));
        return analysis.getKeywordMatches().stream().map(this::mapKeywordMatch).toList();
    }

    @Transactional(readOnly = true)
    public List<RecommendationDto> getRecommendations(Long id) {
        ResumeAnalysis analysis = analysisRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analysis not found with ID: " + id));
        return analysis.getRecommendations().stream().map(this::mapRecommendation).toList();
    }

    @Transactional(readOnly = true)
    public List<AnalysisResponseDto> getUserAnalyses(Long userId) {
        return analysisRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    public AnalysisResponseDto mapToDto(ResumeAnalysis entity) {
        AnalysisResponseDto dto = new AnalysisResponseDto();
        dto.setId(entity.getId());
        dto.setResumeId(entity.getResume().getId());
        dto.setResumeTitle(entity.getResume().getTitle());
        dto.setJobDescriptionId(entity.getJobDescription().getId());
        dto.setJobTitle(entity.getJobDescription().getTitle());
        dto.setCompany(entity.getJobDescription().getCompany());
        dto.setOverallScore(entity.getOverallScore());
        dto.setScoreCategory(entity.getScoreCategory());
        dto.setStatus(entity.getStatus());
        dto.setFormattingScore(entity.getFormattingScore());
        dto.setProcessingDurationMs(entity.getProcessingDurationMs());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getScoreBreakdown() != null) {
            dto.setScoreBreakdown(mapScoreBreakdown(entity.getScoreBreakdown()));
        }

        if (entity.getKeywordMatches() != null) {
            dto.setKeywordMatches(entity.getKeywordMatches().stream().map(this::mapKeywordMatch).toList());
        }

        if (entity.getRecommendations() != null) {
            dto.setRecommendations(entity.getRecommendations().stream().map(this::mapRecommendation).toList());
        }

        try {
            if (entity.getStrengthsJson() != null) {
                dto.setTopStrengths(objectMapper.readValue(entity.getStrengthsJson(), new TypeReference<List<String>>() {}));
            }
            if (entity.getGapsJson() != null) {
                dto.setCriticalGaps(objectMapper.readValue(entity.getGapsJson(), new TypeReference<List<String>>() {}));
            }
            if (entity.getSectionScoresJson() != null) {
                dto.setSectionScores(objectMapper.readValue(entity.getSectionScoresJson(), new TypeReference<Map<String, Double>>() {}));
            }
        } catch (Exception ignored) {}

        return dto;
    }

    private ScoreBreakdownDto mapScoreBreakdown(ScoreBreakdown sb) {
        if (sb == null) return null;
        ScoreBreakdownDto dto = new ScoreBreakdownDto();
        dto.setKeywordMatchScore(sb.getKeywordMatchScore());
        dto.setRequiredSkillsScore(sb.getRequiredSkillsScore());
        dto.setExperienceAlignmentScore(sb.getExperienceAlignmentScore());
        dto.setJobTitleAlignmentScore(sb.getJobTitleAlignmentScore());
        dto.setSemanticSimilarityScore(sb.getSemanticSimilarityScore());
        dto.setResumeStructureScore(sb.getResumeStructureScore());
        dto.setAchievementQualityScore(sb.getAchievementQualityScore());
        dto.setFormattingCompatibilityScore(sb.getFormattingCompatibilityScore());

        if (sb.getBreakdownDetailsJson() != null) {
            try {
                Map<String, CategoryBreakdownDetail> details = objectMapper.readValue(
                        sb.getBreakdownDetailsJson(),
                        new TypeReference<Map<String, CategoryBreakdownDetail>>() {}
                );
                dto.setBreakdownDetails(details);
            } catch (Exception ignored) {}
        }
        return dto;
    }

    private KeywordMatchDto mapKeywordMatch(KeywordMatch km) {
        KeywordMatchDto dto = new KeywordMatchDto();
        dto.setId(km.getId());
        dto.setKeyword(km.getKeyword());
        dto.setMatchStatus(km.getMatchStatus());
        dto.setSkillCategory(km.getSkillCategory());
        dto.setPriorityLevel(km.getPriorityLevel());
        dto.setRelatedTerm(km.getRelatedTerm());
        dto.setImportanceExplanation(km.getImportanceExplanation());
        dto.setIsMustHave(km.getIsMustHave());
        return dto;
    }

    private RecommendationDto mapRecommendation(Recommendation r) {
        RecommendationDto dto = new RecommendationDto();
        dto.setId(r.getId());
        dto.setPriorityLevel(r.getPriorityLevel());
        dto.setSectionTarget(r.getSectionTarget());
        dto.setTitle(r.getTitle());
        dto.setDescription(r.getDescription());
        dto.setAntiFraudNote(r.getAntiFraudNote());
        dto.setOriginalBullet(r.getOriginalBullet());
        dto.setProblemIdentified(r.getProblemIdentified());
        dto.setImprovedBullet(r.getImprovedBullet());
        dto.setWhyBetter(r.getWhyBetter());
        dto.setIsApplied(r.getIsApplied());
        return dto;
    }
}
