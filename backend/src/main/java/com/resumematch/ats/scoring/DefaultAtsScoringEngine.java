package com.resumematch.ats.scoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumematch.ai.provider.AIProvider;
import com.resumematch.ai.provider.AIProviderFactory;
import com.resumematch.ai.provider.AiRecommendationDto;
import com.resumematch.ai.provider.BulletImprovementDto;
import com.resumematch.ats.AtsScoringEngine;
import com.resumematch.ats.keyword.KeywordExtractionEngine;
import com.resumematch.ats.semantic.SemanticSimilarityService;
import com.resumematch.config.AtsProperties;
import com.resumematch.domain.MatchStatus;
import com.resumematch.domain.PriorityLevel;
import com.resumematch.entity.JobDescription;
import com.resumematch.entity.KeywordMatch;
import com.resumematch.entity.Recommendation;
import com.resumematch.entity.Resume;
import com.resumematch.parser.ResumeParserFactory;
import com.resumematch.parser.ResumeTextParserHelper;
import com.resumematch.parser.model.FormattingInspection;
import com.resumematch.parser.model.ResumeContent;
import com.resumematch.parser.model.WorkExperience;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DefaultAtsScoringEngine implements AtsScoringEngine {

    private final AtsProperties properties;
    private final KeywordExtractionEngine keywordEngine;
    private final SemanticSimilarityService semanticService;
    private final AIProviderFactory aiProviderFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DefaultAtsScoringEngine(
            AtsProperties properties,
            KeywordExtractionEngine keywordEngine,
            SemanticSimilarityService semanticService,
            AIProviderFactory aiProviderFactory
    ) {
        this.properties = properties;
        this.keywordEngine = keywordEngine;
        this.semanticService = semanticService;
        this.aiProviderFactory = aiProviderFactory;
    }

    @Override
    public AtsScore analyze(Resume resume, JobDescription jobDescription) {
        AtsScore score = new AtsScore();

        String resumeText = resume.getRawText() != null ? resume.getRawText() : "";
        String jdText = jobDescription.getRawText() != null ? jobDescription.getRawText() : "";

        // Parse structured content if not already stored
        ResumeContent content;
        try {
            if (resume.getParsedJson() != null && !resume.getParsedJson().isBlank()) {
                content = objectMapper.readValue(resume.getParsedJson(), ResumeContent.class);
            } else {
                content = ResumeTextParserHelper.parseFromText(resumeText);
            }
        } catch (Exception e) {
            content = ResumeTextParserHelper.parseFromText(resumeText);
        }

        // 1. Keyword & Skills Matching
        List<KeywordMatch> allKeywordMatches = keywordEngine.extractAndMatch(resumeText, jdText);
        score.setKeywordMatches(allKeywordMatches);

        // 1a. Keyword Match Calculation
        long totalKws = allKeywordMatches.size();
        long matchedKws = allKeywordMatches.stream().filter(k -> k.getMatchStatus() == MatchStatus.MATCHED).count();
        long relatedKws = allKeywordMatches.stream().filter(k -> k.getMatchStatus() == MatchStatus.RELATED).count();

        double keywordScore = totalKws == 0 ? 80.0 : ((matchedKws + (relatedKws * 0.70)) / (double) totalKws) * 100.0;
        keywordScore = clamp(keywordScore);
        score.setKeywordMatchScore(round(keywordScore));

        CategoryBreakdownDetail kwDetail = new CategoryBreakdownDetail(
                "KEYWORD_MATCH", "Keyword Match", keywordScore, properties.getKeywordMatch(),
                "Measures the exact and contextual representation of technologies and competencies from the job description in your resume."
        );
        kwDetail.setWhatDetected(allKeywordMatches.stream()
                .filter(k -> k.getMatchStatus() == MatchStatus.MATCHED)
                .map(KeywordMatch::getKeyword)
                .limit(10).collect(Collectors.toList()));
        kwDetail.setWhatMissing(allKeywordMatches.stream()
                .filter(k -> k.getMatchStatus() == MatchStatus.MISSING)
                .map(KeywordMatch::getKeyword)
                .limit(10).collect(Collectors.toList()));
        kwDetail.setHowToImprove(List.of(
                "Integrate high-frequency missing terms into your Skills list and relevant project descriptions.",
                "Ensure standard industry naming (e.g. use 'Apache Kafka' or 'Kafka' consistently).",
                "Only add keywords you have genuine hands-on experience with."
        ));
        score.getBreakdownDetails().put("KEYWORD_MATCH", kwDetail);

        // 1b. Required Skills Score
        List<KeywordMatch> mustHaves = allKeywordMatches.stream()
                .filter(k -> Boolean.TRUE.equals(k.getIsMustHave()))
                .toList();
        double requiredSkillsScore;
        if (mustHaves.isEmpty()) {
            requiredSkillsScore = keywordScore;
        } else {
            long matchedMust = mustHaves.stream().filter(k -> k.getMatchStatus() == MatchStatus.MATCHED).count();
            long relatedMust = mustHaves.stream().filter(k -> k.getMatchStatus() == MatchStatus.RELATED).count();
            requiredSkillsScore = clamp(((matchedMust + (relatedMust * 0.65)) / (double) mustHaves.size()) * 100.0);
        }
        score.setRequiredSkillsScore(round(requiredSkillsScore));

        CategoryBreakdownDetail skillsDetail = new CategoryBreakdownDetail(
                "REQUIRED_SKILLS", "Required Skills", requiredSkillsScore, properties.getRequiredSkills(),
                "Assesses compliance with non-negotiable core technologies and prerequisites flagged as mandatory."
        );
        skillsDetail.setWhatDetected(mustHaves.stream().filter(k -> k.getMatchStatus() != MatchStatus.MISSING).map(KeywordMatch::getKeyword).toList());
        skillsDetail.setWhatMissing(mustHaves.stream().filter(k -> k.getMatchStatus() == MatchStatus.MISSING).map(KeywordMatch::getKeyword).toList());
        skillsDetail.setHowToImprove(List.of(
                "Highlight mandatory must-have skills at the top of your resume and within recent employment history.",
                "Verify whether related skills can be clarified to reflect exact required tools."
        ));
        score.getBreakdownDetails().put("REQUIRED_SKILLS", skillsDetail);

        // 2. Experience Alignment Score
        double experienceScore = calculateExperienceAlignment(content, jdText);
        score.setExperienceAlignmentScore(round(experienceScore));

        CategoryBreakdownDetail expDetail = new CategoryBreakdownDetail(
                "EXPERIENCE_ALIGNMENT", "Experience Alignment", experienceScore, properties.getExperienceAlignment(),
                "Evaluates candidate seniority level and demonstrated years of experience against job requirements."
        );
        expDetail.setWhatDetected(List.of("Detected ~" + content.getEstimatedYearsExperience() + " years of cumulative experience."));
        expDetail.setHowToImprove(List.of(
                "Ensure employment dates (month/year) are clearly specified for each role.",
                "Emphasize progressive career growth and leadership scope in recent positions."
        ));
        score.getBreakdownDetails().put("EXPERIENCE_ALIGNMENT", expDetail);

        // 3. Job Title Alignment
        double titleScore = calculateJobTitleAlignment(content, jobDescription.getTitle(), jdText);
        score.setJobTitleAlignmentScore(round(titleScore));

        CategoryBreakdownDetail titleDetail = new CategoryBreakdownDetail(
                "JOB_TITLE_ALIGNMENT", "Job Title Alignment", titleScore, properties.getJobTitleAlignment(),
                "Checks whether your past job titles and professional summary headline correspond to the target role title."
        );
        titleDetail.setWhatDetected(List.of("Target role: " + jobDescription.getTitle()));
        titleDetail.setHowToImprove(List.of(
                "Include the target job title in your professional summary headline.",
                "Ensure past titles clearly communicate your functional engineering discipline."
        ));
        score.getBreakdownDetails().put("JOB_TITLE_ALIGNMENT", titleDetail);

        // 4. Semantic Similarity
        double semanticScore = semanticService.calculateCosineSimilarity(resumeText, jdText);
        score.setSemanticSimilarityScore(round(semanticScore));

        CategoryBreakdownDetail semDetail = new CategoryBreakdownDetail(
                "SEMANTIC_SIMILARITY", "Semantic Similarity", semanticScore, properties.getSemanticSimilarity(),
                "Measures conceptual and contextual overlap between resume accomplishments and job responsibilities via vector embeddings."
        );
        semDetail.setHowToImprove(List.of(
                "Mirror the domain terminology and problem-solving phrasing found in the job responsibilities.",
                "Describe project challenges and technical solutions using standard architectural vernacular."
        ));
        score.getBreakdownDetails().put("SEMANTIC_SIMILARITY", semDetail);

        // 5. Resume Structure
        double structureScore = calculateStructureScore(content);
        score.setResumeStructureScore(round(structureScore));

        CategoryBreakdownDetail structDetail = new CategoryBreakdownDetail(
                "RESUME_STRUCTURE", "Resume Structure", structureScore, properties.getResumeStructure(),
                "Audits standard resume layout sections required for accurate parsing by Applicant Tracking Systems."
        );
        structDetail.setWhatDetected(List.of("Sections parsed: Summary, Skills, Work Experience, Education, Projects."));
        structDetail.setHowToImprove(List.of(
                "Maintain standard section headers (Professional Summary, Technical Skills, Experience, Education).",
                "Ensure chronological order with most recent experience first."
        ));
        score.getBreakdownDetails().put("RESUME_STRUCTURE", structDetail);

        // 6. Achievement Quality
        double achievementScore = calculateAchievementQuality(content);
        score.setAchievementQualityScore(round(achievementScore));

        CategoryBreakdownDetail achDetail = new CategoryBreakdownDetail(
                "ACHIEVEMENT_QUALITY", "Achievement Quality", achievementScore, properties.getAchievementQuality(),
                "Scans bullet points for strong action verbs, quantifiable metrics, scalability indicators, and business impact."
        );
        achDetail.setHowToImprove(List.of(
                "Adopt the Google 'X-Y-Z' formula: Accomplished [X], as measured by [Y], by doing [Z].",
                "Incorporate tangible metrics (%, $, time saved, throughput, user count).",
                "Replace passive phrases ('Assisted with', 'Worked on') with strong active verbs ('Architected', 'Spearheaded', 'Optimized')."
        ));
        score.getBreakdownDetails().put("ACHIEVEMENT_QUALITY", achDetail);

        // 7. Formatting Compatibility
        FormattingInspection formatting = content.getFormattingInspection();
        double formattingScore = formatting != null ? formatting.getScore() : 90.0;
        score.setFormattingCompatibilityScore(round(formattingScore));

        CategoryBreakdownDetail fmtDetail = new CategoryBreakdownDetail(
                "FORMATTING_COMPATIBILITY", "Formatting Compatibility", formattingScore, properties.getFormattingCompatibility(),
                "Checks machine readability and flags layout traps like tables, multiple columns, graphics, or complex headers."
        );
        if (formatting != null) {
            fmtDetail.setWhatDetected(formatting.getPassedChecks());
            fmtDetail.setWhatMissing(formatting.getWarningIssues());
        }
        fmtDetail.setHowToImprove(List.of(
                "Use a clean single-column layout without tables or text boxes.",
                "Stick to standard ATS-compatible typography."
        ));
        score.getBreakdownDetails().put("FORMATTING_COMPATIBILITY", fmtDetail);

        // Calculate Overall ATS Score
        double overall = (keywordScore * properties.getKeywordMatch()) +
                (requiredSkillsScore * properties.getRequiredSkills()) +
                (experienceScore * properties.getExperienceAlignment()) +
                (titleScore * properties.getJobTitleAlignment()) +
                (semanticScore * properties.getSemanticSimilarity()) +
                (structureScore * properties.getResumeStructure()) +
                (achievementScore * properties.getAchievementQuality()) +
                (formattingScore * properties.getFormattingCompatibility());

        score.setOverallScore(clamp(overall));

        // Strengths & Gaps
        populateStrengthsAndGaps(score, allKeywordMatches, content, jobDescription);

        // AI Enrichment: Recommendations and Bullet Improvements
        enrichWithAi(score, resume, jobDescription, content, allKeywordMatches);

        // Section Scores
        Map<String, Double> sections = new LinkedHashMap<>();
        sections.put("Summary", round(clamp(titleScore * 0.5 + semanticScore * 0.5)));
        sections.put("Experience", round(clamp(experienceScore * 0.4 + achievementScore * 0.6)));
        sections.put("Skills", round(clamp(keywordScore * 0.6 + requiredSkillsScore * 0.4)));
        sections.put("Education", 95.0);
        sections.put("Projects", round(clamp(semanticScore * 0.7 + achievementScore * 0.3)));
        sections.put("Certifications", content.getCertifications().isEmpty() ? 60.0 : 90.0);
        score.setSectionScores(sections);

        return score;
    }

    private void populateStrengthsAndGaps(AtsScore score, List<KeywordMatch> matches, ResumeContent content, JobDescription jd) {
        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        for (KeywordMatch m : matches) {
            if (m.getMatchStatus() == MatchStatus.MATCHED && strengths.size() < 6) {
                strengths.add("✓ " + m.getKeyword() + " demonstrated and matched from job requirements.");
            } else if (m.getMatchStatus() == MatchStatus.MISSING && gaps.size() < 5) {
                gaps.add("⚠ " + m.getKeyword() + " not detected in your resume.");
            }
        }

        if (content.getFormattingInspection().isHasStandardHeadings()) {
            strengths.add("✓ Standard ATS-compliant section headings verified.");
        }
        if (content.getEstimatedYearsExperience() >= 3.0) {
            strengths.add("✓ Substantial multi-year professional career history detected.");
        }

        if (score.getAchievementQualityScore() < 75.0) {
            gaps.add("⚠ Several experience bullets lack quantifiable business impact metrics.");
        }
        if (score.getJobTitleAlignmentScore() < 80.0) {
            gaps.add("⚠ Target job title '" + jd.getTitle() + "' is not explicitly reflected in your summary.");
        }

        score.setTopStrengths(strengths);
        score.setCriticalGaps(gaps);
    }

    private void enrichWithAi(AtsScore score, Resume resume, JobDescription jd, ResumeContent content, List<KeywordMatch> matches) {
        AIProvider aiProvider = aiProviderFactory.getProvider();

        List<String> missingKeywords = matches.stream()
                .filter(k -> k.getMatchStatus() == MatchStatus.MISSING)
                .map(KeywordMatch::getKeyword)
                .toList();

        // Recommendations
        List<AiRecommendationDto> aiRecs = aiProvider.generateRecommendations(resume.getRawText(), jd.getRawText(), missingKeywords);
        List<Recommendation> entityRecs = new ArrayList<>();

        for (AiRecommendationDto dto : aiRecs) {
            Recommendation rec = new Recommendation();
            rec.setPriorityLevel(dto.getPriority());
            rec.setSectionTarget(dto.getCategory());
            rec.setTitle(dto.getTitle());
            rec.setDescription(dto.getDescription());
            rec.setAntiFraudNote(dto.getAntiFraudNote());
            entityRecs.add(rec);
        }

        // Bullet improvements
        List<String> rawBullets = new ArrayList<>();
        for (WorkExperience exp : content.getExperiences()) {
            rawBullets.addAll(exp.getBulletPoints());
        }
        if (rawBullets.isEmpty()) {
            rawBullets.add("Worked on software development and team tasks.");
        }

        List<BulletImprovementDto> bulletImprovements = aiProvider.analyzeAndImproveBullets(
                rawBullets.subList(0, Math.min(4, rawBullets.size())),
                jd.getTitle(),
                matches.stream().filter(k -> k.getMatchStatus() == MatchStatus.MATCHED).map(KeywordMatch::getKeyword).toList()
        );

        for (BulletImprovementDto b : bulletImprovements) {
            Recommendation bulletRec = new Recommendation();
            bulletRec.setPriorityLevel(PriorityLevel.MEDIUM);
            bulletRec.setSectionTarget("EXPERIENCE");
            bulletRec.setTitle("Strengthen experience bullet with action verb and metrics");
            bulletRec.setDescription(b.getWhyBetter());
            bulletRec.setOriginalBullet(b.getOriginal());
            bulletRec.setProblemIdentified(b.getProblem());
            bulletRec.setImprovedBullet(b.getImproved());
            bulletRec.setWhyBetter(b.getWhyBetter());
            bulletRec.setAntiFraudNote("Replace placeholder numbers with your genuine metrics. Do not fabricate figures.");
            entityRecs.add(bulletRec);
        }

        score.setRecommendations(entityRecs);
    }

    private double calculateExperienceAlignment(ResumeContent content, String jdText) {
        double candidateYears = content.getEstimatedYearsExperience();
        double requiredYears = 3.0; // default baseline

        Pattern p = Pattern.compile("(\\d+)\\+?\\s*(?:years|yrs)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(jdText);
        if (m.find()) {
            try {
                requiredYears = Double.parseDouble(m.group(1));
            } catch (Exception ignored) {}
        }

        if (candidateYears >= requiredYears) {
            return 95.0;
        } else if (candidateYears >= requiredYears * 0.7) {
            return 80.0;
        } else {
            return Math.max(50.0, (candidateYears / requiredYears) * 85.0);
        }
    }

    private double calculateJobTitleAlignment(ResumeContent content, String targetTitle, String jdText) {
        if (targetTitle == null || targetTitle.isBlank()) {
            return 85.0;
        }

        String targetLower = targetTitle.toLowerCase();
        // Check in summary
        if (content.getSummary().toLowerCase().contains(targetLower)) {
            return 95.0;
        }
        // Check in work experiences
        for (WorkExperience exp : content.getExperiences()) {
            if (exp.getJobTitle() != null && exp.getJobTitle().toLowerCase().contains(targetLower)) {
                return 92.0;
            }
        }
        // Partial token check
        String[] tokens = targetLower.split("\\s+");
        long matchedTokens = Arrays.stream(tokens)
                .filter(t -> t.length() > 2 && (content.getSummary().toLowerCase().contains(t) || content.getRawText().toLowerCase().contains(t)))
                .count();

        if (tokens.length > 0 && matchedTokens > 0) {
            return clamp(((double) matchedTokens / tokens.length) * 85.0);
        }
        return 65.0;
    }

    private double calculateStructureScore(ResumeContent content) {
        double score = 60.0;
        if (!content.getSummary().isBlank()) score += 10.0;
        if (!content.getExperiences().isEmpty()) score += 10.0;
        if (!content.getSkills().isEmpty()) score += 10.0;
        if (!content.getEducations().isEmpty()) score += 5.0;
        if (!content.getProjects().isEmpty() || !content.getCertifications().isEmpty()) score += 5.0;
        return clamp(score);
    }

    private double calculateAchievementQuality(ResumeContent content) {
        List<String> bullets = new ArrayList<>();
        for (WorkExperience exp : content.getExperiences()) {
            bullets.addAll(exp.getBulletPoints());
        }

        if (bullets.isEmpty()) {
            return 60.0;
        }

        long bulletsWithMetrics = bullets.stream()
                .filter(b -> b.matches(".*\\d+%.*") || b.matches(".*\\d+\\+?.*") || b.matches(".*\\$\\d+.*"))
                .count();

        long bulletsWithStrongVerbs = bullets.stream()
                .filter(b -> {
                    String lower = b.toLowerCase();
                    return lower.startsWith("architect") || lower.startsWith("develop") || lower.startsWith("design")
                            || lower.startsWith("engineer") || lower.startsWith("spearhead") || lower.startsWith("optimiz")
                            || lower.startsWith("implement") || lower.startsWith("deliver") || lower.startsWith("scale");
                })
                .count();

        double metricRatio = (double) bulletsWithMetrics / bullets.size();
        double verbRatio = (double) bulletsWithStrongVerbs / bullets.size();

        double qualityScore = 50.0 + (metricRatio * 30.0) + (verbRatio * 20.0);
        return clamp(qualityScore);
    }

    private double clamp(double value) {
        return Math.max(10.0, Math.min(100.0, value));
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
