package com.resumematch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumematch.ai.provider.AIProvider;
import com.resumematch.ai.provider.AIProviderFactory;
import com.resumematch.ai.provider.BulletImprovementDto;
import com.resumematch.ai.provider.SummaryImprovementDto;
import com.resumematch.domain.PriorityLevel;
import com.resumematch.dto.OptimizationResponseDto;
import com.resumematch.dto.RecommendationDto;
import com.resumematch.entity.Resume;
import com.resumematch.entity.ResumeVersion;
import com.resumematch.exception.ResourceNotFoundException;
import com.resumematch.parser.ResumeTextParserHelper;
import com.resumematch.parser.model.ResumeContent;
import com.resumematch.parser.model.WorkExperience;
import com.resumematch.repository.ResumeRepository;
import com.resumematch.repository.ResumeVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImprovementService {

    private final ResumeRepository resumeRepository;
    private final ResumeVersionRepository versionRepository;
    private final AIProviderFactory aiProviderFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ImprovementService(
            ResumeRepository resumeRepository,
            ResumeVersionRepository versionRepository,
            AIProviderFactory aiProviderFactory
    ) {
        this.resumeRepository = resumeRepository;
        this.versionRepository = versionRepository;
        this.aiProviderFactory = aiProviderFactory;
    }

    @Transactional(readOnly = true)
    public OptimizationResponseDto getImprovements(Long resumeId, String targetRole) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

        ResumeContent content;
        try {
            if (resume.getParsedJson() != null && !resume.getParsedJson().isBlank()) {
                content = objectMapper.readValue(resume.getParsedJson(), ResumeContent.class);
            } else {
                content = ResumeTextParserHelper.parseFromText(resume.getRawText());
            }
        } catch (Exception e) {
            content = ResumeTextParserHelper.parseFromText(resume.getRawText());
        }

        AIProvider ai = aiProviderFactory.getProvider();
        String role = (targetRole != null && !targetRole.isBlank()) ? targetRole : "Senior Software Engineer";

        // Summary improvement
        SummaryImprovementDto summaryDto = ai.improveSummary(content.getSummary(), role, content.getSkills());

        // Bullet improvements
        List<String> bullets = new ArrayList<>();
        for (WorkExperience exp : content.getExperiences()) {
            bullets.addAll(exp.getBulletPoints());
        }
        if (bullets.isEmpty()) {
            bullets.add("Worked on software development and platform features.");
        }

        List<BulletImprovementDto> bulletImprovements = ai.analyzeAndImproveBullets(
                bullets.subList(0, Math.min(5, bullets.size())),
                role,
                content.getTechnicalSkills()
        );

        List<RecommendationDto> bulletRecDtos = new ArrayList<>();
        long recId = 1;
        for (BulletImprovementDto b : bulletImprovements) {
            RecommendationDto dto = new RecommendationDto();
            dto.setId(recId++);
            dto.setPriorityLevel(PriorityLevel.HIGH);
            dto.setSectionTarget("EXPERIENCE");
            dto.setTitle("Strengthen action verb and include quantifiable outcome");
            dto.setDescription(b.getWhyBetter());
            dto.setOriginalBullet(b.getOriginal());
            dto.setProblemIdentified(b.getProblem());
            dto.setImprovedBullet(b.getImproved());
            dto.setWhyBetter(b.getWhyBetter());
            dto.setAntiFraudNote("Replace bracketed placeholders with your actual measured impact. Do not invent metrics.");
            dto.setIsApplied(false);
            bulletRecDtos.add(dto);
        }

        OptimizationResponseDto response = new OptimizationResponseDto();
        response.setResumeId(resumeId);
        response.setTargetJobTitle(role);
        response.setOriginalSummary(summaryDto.getOriginalSummary());
        response.setImprovedSummary(summaryDto.getImprovedSummary());
        response.setSummaryRationale(summaryDto.getRationale());
        response.setBulletImprovements(bulletRecDtos);

        return response;
    }

    @Transactional
    public Resume optimizeAndSaveVersion(Long resumeId, Map<String, String> appliedChanges) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + resumeId));

        ResumeContent content;
        try {
            content = ResumeTextParserHelper.parseFromText(resume.getRawText());
        } catch (Exception e) {
            content = new ResumeContent();
        }

        // Apply summary if changed
        if (appliedChanges.containsKey("summary")) {
            content.setSummary(appliedChanges.get("summary"));
        }

        // Apply bullet replacements
        for (Map.Entry<String, String> entry : appliedChanges.entrySet()) {
            if (entry.getKey().startsWith("bullet_")) {
                String original = entry.getKey().substring(7);
                String replacement = entry.getValue();
                for (WorkExperience exp : content.getExperiences()) {
                    List<String> bullets = exp.getBulletPoints();
                    for (int i = 0; i < bullets.size(); i++) {
                        if (bullets.get(i).contains(original) || original.contains(bullets.get(i))) {
                            bullets.set(i, replacement);
                        }
                    }
                }
            }
        }

        // Save new version
        ResumeVersion version = new ResumeVersion();
        version.setResume(resume);
        version.setVersionNumber(resume.getVersions().size() + 1);
        version.setVersionName("Optimized ATS Version " + version.getVersionNumber());
        version.setContentText(resume.getRawText());
        try {
            version.setContentJson(objectMapper.writeValueAsString(content));
        } catch (Exception ignored) {}

        versionRepository.save(version);
        resume.getVersions().add(version);

        return resumeRepository.save(resume);
    }
}
