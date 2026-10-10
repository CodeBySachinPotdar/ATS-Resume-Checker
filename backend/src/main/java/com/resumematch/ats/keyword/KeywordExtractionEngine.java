package com.resumematch.ats.keyword;

import com.resumematch.domain.MatchStatus;
import com.resumematch.domain.PriorityLevel;
import com.resumematch.domain.SkillCategory;
import com.resumematch.entity.KeywordMatch;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class KeywordExtractionEngine {

    // Technical skills dictionary
    private static final Map<String, SkillCategory> KNOWN_TECH_KEYWORDS = new LinkedHashMap<>();
    // Soft skills dictionary
    private static final Set<String> KNOWN_SOFT_SKILLS = new LinkedHashSet<>();
    // Semantic relationships: Target JD term -> list of candidate synonyms/related terms
    private static final Map<String, List<String>> SEMANTIC_SYNONYMS = new LinkedHashMap<>();

    static {
        // Tech keywords
        registerTech("Java", SkillCategory.TECHNICAL);
        registerTech("Spring Boot", SkillCategory.TOOL_FRAMEWORK);
        registerTech("Microservices", SkillCategory.TECHNICAL);
        registerTech("Apache Kafka", SkillCategory.DEVOPS_CLOUD);
        registerTech("Kafka", SkillCategory.DEVOPS_CLOUD);
        registerTech("Docker", SkillCategory.DEVOPS_CLOUD);
        registerTech("Kubernetes", SkillCategory.DEVOPS_CLOUD);
        registerTech("AWS", SkillCategory.DEVOPS_CLOUD);
        registerTech("Amazon Web Services", SkillCategory.DEVOPS_CLOUD);
        registerTech("Azure", SkillCategory.DEVOPS_CLOUD);
        registerTech("GCP", SkillCategory.DEVOPS_CLOUD);
        registerTech("PostgreSQL", SkillCategory.DATABASE);
        registerTech("Redis", SkillCategory.DATABASE);
        registerTech("MongoDB", SkillCategory.DATABASE);
        registerTech("MySQL", SkillCategory.DATABASE);
        registerTech("Oracle", SkillCategory.DATABASE);
        registerTech("Hibernate", SkillCategory.TOOL_FRAMEWORK);
        registerTech("Spring Data JPA", SkillCategory.TOOL_FRAMEWORK);
        registerTech("RESTful Web Services", SkillCategory.TECHNICAL);
        registerTech("REST APIs", SkillCategory.TECHNICAL);
        registerTech("GraphQL", SkillCategory.TECHNICAL);
        registerTech("gRPC", SkillCategory.TECHNICAL);
        registerTech("Python", SkillCategory.TECHNICAL);
        registerTech("TypeScript", SkillCategory.TECHNICAL);
        registerTech("JavaScript", SkillCategory.TECHNICAL);
        registerTech("React", SkillCategory.TOOL_FRAMEWORK);
        registerTech("Angular", SkillCategory.TOOL_FRAMEWORK);
        registerTech("Node.js", SkillCategory.TOOL_FRAMEWORK);
        registerTech("CI/CD", SkillCategory.DEVOPS_CLOUD);
        registerTech("Terraform", SkillCategory.DEVOPS_CLOUD);
        registerTech("Git", SkillCategory.TOOL_FRAMEWORK);
        registerTech("JUnit", SkillCategory.TOOL_FRAMEWORK);
        registerTech("Testcontainers", SkillCategory.TOOL_FRAMEWORK);
        registerTech("Event-Driven Architecture", SkillCategory.TECHNICAL);
        registerTech("Distributed Systems", SkillCategory.TECHNICAL);
        registerTech("System Design", SkillCategory.TECHNICAL);
        registerTech("Clean Architecture", SkillCategory.METHODOLOGY);
        registerTech("Design Patterns", SkillCategory.METHODOLOGY);
        registerTech("Elasticsearch", SkillCategory.DATABASE);
        registerTech("Prometheus", SkillCategory.DEVOPS_CLOUD);
        registerTech("Grafana", SkillCategory.DEVOPS_CLOUD);
        registerTech("Spring Security", SkillCategory.TOOL_FRAMEWORK);
        registerTech("OAuth2", SkillCategory.TECHNICAL);
        registerTech("JWT", SkillCategory.TECHNICAL);
        registerTech("Linux", SkillCategory.TECHNICAL);

        // Soft skills
        KNOWN_SOFT_SKILLS.addAll(List.of(
                "Leadership", "Communication", "Problem Solving", "Team Collaboration",
                "Mentorship", "Agile", "Scrum", "Critical Thinking", "Ownership",
                "Stakeholder Management", "Adaptability", "Cross-functional Leadership"
        ));

        // Synonyms
        SEMANTIC_SYNONYMS.put("RESTful Web Services", List.of("REST APIs", "REST API", "RESTful APIs", "REST"));
        SEMANTIC_SYNONYMS.put("REST APIs", List.of("RESTful Web Services", "RESTful API", "REST"));
        SEMANTIC_SYNONYMS.put("Kubernetes", List.of("K8s", "Container Orchestration", "EKS", "GKE"));
        SEMANTIC_SYNONYMS.put("PostgreSQL", List.of("Postgres", "Postgres DB", "RDBMS"));
        SEMANTIC_SYNONYMS.put("AWS", List.of("Amazon Web Services", "EC2", "S3", "Cloud Computing"));
        SEMANTIC_SYNONYMS.put("Kafka", List.of("Apache Kafka", "Event-Driven", "Message Broker", "Event Streaming"));
        SEMANTIC_SYNONYMS.put("Microservices", List.of("Distributed Systems", "Service-Oriented", "Microservice"));
        SEMANTIC_SYNONYMS.put("CI/CD", List.of("Continuous Integration", "Continuous Delivery", "GitHub Actions", "Jenkins", "GitLab CI"));
        SEMANTIC_SYNONYMS.put("Spring Boot", List.of("Spring Framework", "Spring MVC", "Spring"));
        SEMANTIC_SYNONYMS.put("System Design", List.of("Scalability", "High Availability", "Architecture", "Distributed Systems"));
        SEMANTIC_SYNONYMS.put("Agile", List.of("Scrum", "Kanban", "Sprint Planning"));
    }

    private static void registerTech(String term, SkillCategory cat) {
        KNOWN_TECH_KEYWORDS.put(term, cat);
    }

    public List<KeywordMatch> extractAndMatch(String resumeText, String jdText) {
        List<KeywordMatch> matches = new ArrayList<>();
        if (resumeText == null || jdText == null) return matches;

        String resumeLower = resumeText.toLowerCase();
        String jdLower = jdText.toLowerCase();

        // 1. Identify JD Technical terms
        for (Map.Entry<String, SkillCategory> entry : KNOWN_TECH_KEYWORDS.entrySet()) {
            String term = entry.getKey();
            SkillCategory category = entry.getValue();

            if (containsTerm(jdLower, term.toLowerCase())) {
                boolean isMustHave = isMustHaveInJD(jdLower, term.toLowerCase());
                KeywordMatch km = new KeywordMatch();
                km.setKeyword(term);
                km.setSkillCategory(category);
                km.setIsMustHave(isMustHave);
                km.setPriorityLevel(isMustHave ? PriorityLevel.HIGH : PriorityLevel.MEDIUM);

                if (containsTerm(resumeLower, term.toLowerCase())) {
                    km.setMatchStatus(MatchStatus.MATCHED);
                    km.setImportanceExplanation("Direct match: '" + term + "' appears in both job description and your resume.");
                } else {
                    // Check semantic synonyms
                    String relatedFound = findRelatedTerm(resumeLower, term);
                    if (relatedFound != null) {
                        km.setMatchStatus(MatchStatus.RELATED);
                        km.setRelatedTerm(relatedFound);
                        km.setImportanceExplanation("Related concept found: Your resume mentions '" + relatedFound + "', which is closely aligned with JD's '" + term + "'.");
                    } else {
                        km.setMatchStatus(MatchStatus.MISSING);
                        km.setPriorityLevel(isMustHave ? PriorityLevel.CRITICAL : PriorityLevel.HIGH);
                        km.setImportanceExplanation("Missing from resume. Target role specifies '" + term + "'" + (isMustHave ? " as a core/must-have requirement." : "."));
                    }
                }
                matches.add(km);
            }
        }

        // 2. Identify JD Soft Skills
        for (String softSkill : KNOWN_SOFT_SKILLS) {
            if (containsTerm(jdLower, softSkill.toLowerCase())) {
                KeywordMatch km = new KeywordMatch();
                km.setKeyword(softSkill);
                km.setSkillCategory(SkillCategory.SOFT_SKILL);
                km.setIsMustHave(false);
                km.setPriorityLevel(PriorityLevel.MEDIUM);

                if (containsTerm(resumeLower, softSkill.toLowerCase())) {
                    km.setMatchStatus(MatchStatus.MATCHED);
                    km.setImportanceExplanation("Soft skill match: '" + softSkill + "' is present in your resume.");
                } else {
                    km.setMatchStatus(MatchStatus.MISSING);
                    km.setImportanceExplanation("Consider highlighting '" + softSkill + "' through demonstrable collaborative achievements.");
                }
                matches.add(km);
            }
        }

        return matches;
    }

    private boolean containsTerm(String text, String term) {
        String pattern = "\\b" + Pattern.quote(term) + "\\b";
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text).find();
    }

    private String findRelatedTerm(String resumeLower, String targetTerm) {
        List<String> synonyms = SEMANTIC_SYNONYMS.get(targetTerm);
        if (synonyms != null) {
            for (String syn : synonyms) {
                if (containsTerm(resumeLower, syn.toLowerCase())) {
                    return syn;
                }
            }
        }
        return null;
    }

    private boolean isMustHaveInJD(String jdLower, String termLower) {
        Pattern p = Pattern.compile("(?:require[ds]?|must\\s+have|minimum|mandatory|proficiency\\s+in|\\d+\\+?\\s*years?).{0,50}\\b" + Pattern.quote(termLower) + "\\b", Pattern.CASE_INSENSITIVE);
        return p.matcher(jdLower).find();
    }
}
