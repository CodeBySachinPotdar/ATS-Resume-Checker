package com.resumematch.ai.provider;

import com.resumematch.domain.PriorityLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MockAIProvider implements AIProvider {

    @Override
    public String getProviderName() {
        return "mock";
    }

    @Override
    public List<BulletImprovementDto> analyzeAndImproveBullets(List<String> bullets, String targetRole, List<String> targetKeywords) {
        List<BulletImprovementDto> improvements = new ArrayList<>();
        if (bullets == null || bullets.isEmpty()) {
            return improvements;
        }

        for (String bullet : bullets) {
            String lower = bullet.toLowerCase();
            boolean lacksMetrics = !bullet.matches(".*\\d+%.*") && !bullet.matches(".*\\d+\\+?.*") && !bullet.matches(".*\\$\\d+.*");
            boolean startsWithWeakVerb = lower.startsWith("worked on") || lower.startsWith("responsible for") || lower.startsWith("assisted with") || lower.startsWith("helped with");

            if (startsWithWeakVerb || lacksMetrics || bullet.length() < 50) {
                String original = bullet;
                String problem = startsWithWeakVerb
                        ? "Starts with a passive, responsibility-focused phrase ('" + bullet.split("\\s+")[0] + "') and lacks measurable business outcomes."
                        : "Lacks quantifiable metrics, scale indicators, and business impact.";

                String improved;
                if (lower.contains("java") || lower.contains("spring") || lower.contains("api") || lower.contains("backend")) {
                    improved = "Architected and delivered high-throughput Spring Boot REST microservices for real-time transaction processing, reducing API latency by 28% and handling 1.5M+ daily requests.";
                } else if (lower.contains("frontend") || lower.contains("react") || lower.contains("ui")) {
                    improved = "Designed and developed modular React/TypeScript user interfaces, improving Core Web Vitals LCP by 35% and streamlining candidate onboarding for 50,000+ monthly active users.";
                } else if (lower.contains("docker") || lower.contains("ci") || lower.contains("devops") || lower.contains("cloud")) {
                    improved = "Orchestrated containerized CI/CD deployment pipelines using Docker and Kubernetes, reducing build deployment failure rate by 40% and shortening release cycle time from 2 days to 30 minutes.";
                } else {
                    improved = "Spearheaded end-to-end implementation of " + bullet.replaceFirst("^[Ww]orked on\\s*", "").replaceFirst("^[Rr]esponsible for\\s*", "") + ", enhancing system reliability and achieving [X%] performance efficiency gains across [Y] team workflows.";
                }

                String whyBetter = "Replaced passive phrasing with decisive action verb ('Architected'/'Spearheaded'), incorporated industry-standard technologies, highlighted system scale, and introduced quantifiable business impact without fabricating candidate experience.";
                improvements.add(new BulletImprovementDto(original, problem, improved, whyBetter, "EXPERIENCE"));
            }
        }

        // If none met the condition, take the first bullet anyway to provide value
        if (improvements.isEmpty() && !bullets.isEmpty()) {
            String b = bullets.get(0);
            improvements.add(new BulletImprovementDto(
                    b,
                    "Statement can be sharpened by foregrounding business scale and technical ownership.",
                    "Engineered robust solutions for " + b + ", driving measurable gains in code coverage and operational scalability.",
                    "Strengthened engineering agency and highlighted architectural ownership.",
                    "EXPERIENCE"
            ));
        }

        return improvements;
    }

    @Override
    public SummaryImprovementDto improveSummary(String currentSummary, String targetRole, List<String> keySkills) {
        String role = (targetRole != null && !targetRole.isBlank()) ? targetRole : "Senior Software Engineer";
        String skillsStr = (keySkills != null && !keySkills.isEmpty())
                ? String.join(", ", keySkills.subList(0, Math.min(4, keySkills.size())))
                : "Java, Spring Boot, Microservices, Cloud Architecture";

        String improved = String.format(
                "Results-driven %s with demonstrated expertise in engineering scalable, high-availability distributed systems utilizing %s. Proven track record of delivering resilient backend services, optimizing application throughput, and collaborating across agile cross-functional engineering teams.",
                role, skillsStr
        );

        String rationale = "Aligns the professional summary directly with the target job title (" + role + ") and embeds core technical competencies within the first 3 lines, which ATS parsers and human recruiters scan first.";

        return new SummaryImprovementDto(currentSummary, improved, rationale, role);
    }

    @Override
    public List<AiRecommendationDto> generateRecommendations(String resumeText, String jdText, List<String> missingKeywords) {
        List<AiRecommendationDto> recs = new ArrayList<>();

        // Missing critical keywords check with anti-fraud warning
        if (missingKeywords != null && !missingKeywords.isEmpty()) {
            for (int i = 0; i < Math.min(3, missingKeywords.size()); i++) {
                String kw = missingKeywords.get(i);
                recs.add(new AiRecommendationDto(
                        PriorityLevel.HIGH,
                        "SKILLS",
                        "Target keyword '" + kw + "' is prominent in the job description",
                        "The target role repeatedly emphasizes '" + kw + "'. Integrating this into your Technical Skills and relevant project bullets will noticeably boost keyword match scores.",
                        "Anti-Fraud Principle: Only add '" + kw + "' if you have genuine, verifiable hands-on experience. Never fabricate skills to game ATS filters."
                ));
            }
        }

        // Professional summary recommendation
        recs.add(new AiRecommendationDto(
                PriorityLevel.HIGH,
                "SUMMARY",
                "Align professional summary with target job title",
                "Ensure your summary explicitly incorporates the target role title in the first sentence to establish immediate role relevance for ATS parsers.",
                "Ensure your stated seniority and job title genuinely match your professional background."
        ));

        // Measurable impact recommendation
        recs.add(new AiRecommendationDto(
                PriorityLevel.MEDIUM,
                "EXPERIENCE",
                "Quantify bullet points with authentic metrics",
                "Several experience bullets describe routine duties rather than business outcomes. Introduce metrics such as percentage reductions in latency, user volume handled, or deployment time savings.",
                "Never invent numerical figures. Use authentic metrics or bracket placeholders [e.g., improved latency by X%] to remind yourself to look up accurate numbers."
        ));

        // Formatting recommendation
        recs.add(new AiRecommendationDto(
                PriorityLevel.LOW,
                "FORMATTING",
                "Maintain clean linear single-column structure",
                "Avoid graphical tables, complex two-column sidebars, or icons that can confuse older enterprise ATS parsers.",
                null
        ));

        return recs;
    }

    @Override
    public String extractJobRequirementsJson(String jdText) {
        return "{\"title\":\"Software Engineer\",\"experience\":\"3+ years\",\"level\":\"Mid-Senior\"}";
    }

    @Override
    public String extractResumeStructureJson(String resumeText) {
        return "{\"status\":\"STRUCTURED\",\"sections\":[\"Summary\",\"Skills\",\"Experience\",\"Education\"]}";
    }
}
