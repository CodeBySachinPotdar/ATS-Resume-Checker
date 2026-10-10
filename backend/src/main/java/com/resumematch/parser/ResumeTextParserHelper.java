package com.resumematch.parser;

import com.resumematch.parser.model.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResumeTextParserHelper {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
    private static final Pattern LINKEDIN_PATTERN = Pattern.compile("(?:https?:\\/\\/)?(?:www\\.)?linkedin\\.com\\/in\\/[a-zA-Z0-9_-]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern GITHUB_PATTERN = Pattern.compile("(?:https?:\\/\\/)?(?:www\\.)?github\\.com\\/[a-zA-Z0-9_-]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern YEAR_RANGE_PATTERN = Pattern.compile("(20\\d{2}|19\\d{2})\\s*(?:-|–|to)\\s*(present|current|now|20\\d{2})", Pattern.CASE_INSENSITIVE);

    public static ResumeContent parseFromText(String rawText) {
        ResumeContent content = new ResumeContent();
        if (rawText == null || rawText.isBlank()) {
            return content;
        }

        content.setRawText(rawText);
        String[] lines = rawText.lines().map(String::trim).toArray(String[]::new);

        // 1. Extract Contact Info
        ContactInfo contactInfo = extractContactInfo(rawText, lines);
        content.setContactInfo(contactInfo);

        // 2. Identify Sections
        Map<String, List<String>> sections = partitionSections(lines);

        // 3. Extract Summary
        List<String> summaryLines = getSectionContent(sections, "summary", "objective", "about", "profile");
        if (!summaryLines.isEmpty()) {
            content.setSummary(String.join(" ", summaryLines));
        }

        // 4. Extract Skills
        List<String> skillLines = getSectionContent(sections, "skills", "technical skills", "core competencies", "technologies");
        List<String> extractedSkills = extractSkillsList(skillLines);
        content.setSkills(extractedSkills);
        categorizeSkills(extractedSkills, content);

        // 5. Extract Experience
        List<String> expLines = getSectionContent(sections, "experience", "work experience", "employment history", "professional experience");
        List<WorkExperience> experiences = parseWorkExperience(expLines);
        content.setExperiences(experiences);

        // 6. Extract Education
        List<String> eduLines = getSectionContent(sections, "education", "academic background", "qualifications");
        List<EducationItem> educations = parseEducation(eduLines);
        content.setEducations(educations);

        // 7. Extract Certifications
        List<String> certLines = getSectionContent(sections, "certifications", "certificates", "licenses");
        List<CertificationItem> certifications = parseCertifications(certLines);
        content.setCertifications(certifications);

        // 8. Extract Projects
        List<String> projLines = getSectionContent(sections, "projects", "key projects", "personal projects");
        List<ProjectItem> projects = parseProjects(projLines);
        content.setProjects(projects);

        // 9. Extract Achievements
        List<String> achLines = getSectionContent(sections, "achievements", "awards", "honors");
        content.setAchievements(achLines);

        // 10. Estimate Experience
        content.setEstimatedYearsExperience(estimateYears(experiences, rawText));

        // 11. Run Formatting Inspection
        content.setFormattingInspection(inspectFormatting(rawText, lines, sections, contactInfo));

        return content;
    }

    private static ContactInfo extractContactInfo(String rawText, String[] lines) {
        ContactInfo info = new ContactInfo();

        // Email
        Matcher emailMatcher = EMAIL_PATTERN.matcher(rawText);
        if (emailMatcher.find()) {
            info.setEmail(emailMatcher.group());
        }

        // Phone
        Matcher phoneMatcher = PHONE_PATTERN.matcher(rawText);
        if (phoneMatcher.find()) {
            info.setPhone(phoneMatcher.group());
        }

        // LinkedIn
        Matcher linkedInMatcher = LINKEDIN_PATTERN.matcher(rawText);
        if (linkedInMatcher.find()) {
            info.setLinkedIn(linkedInMatcher.group());
        }

        // GitHub
        Matcher githubMatcher = GITHUB_PATTERN.matcher(rawText);
        if (githubMatcher.find()) {
            info.setGitHub(githubMatcher.group());
        }

        // Name heuristics: first non-blank line under 40 chars that doesn't contain email/url/digits
        for (int i = 0; i < Math.min(5, lines.length); i++) {
            String line = lines[i];
            if (!line.isBlank() && line.length() < 40 && !line.contains("@") && !line.toLowerCase().contains("http")
                    && !line.matches(".*\\d.*") && !line.equalsIgnoreCase("resume") && !line.equalsIgnoreCase("curriculum vitae")) {
                info.setName(line);
                break;
            }
        }

        // Location heuristics
        Pattern locPattern = Pattern.compile("([A-Z][a-zA-Z\\s]+),\\s*([A-Z]{2}|[A-Z][a-zA-Z]+)");
        Matcher locMatcher = locPattern.matcher(rawText);
        if (locMatcher.find()) {
            info.setLocation(locMatcher.group());
        }

        return info;
    }

    private static Map<String, List<String>> partitionSections(String[] lines) {
        Map<String, List<String>> sections = new LinkedHashMap<>();
        String currentSection = "header";
        sections.put(currentSection, new ArrayList<>());

        Set<String> commonHeadings = Set.of(
                "summary", "professional summary", "executive summary", "objective", "about me", "profile",
                "skills", "technical skills", "core competencies", "technologies", "key skills",
                "experience", "work experience", "employment history", "professional experience",
                "education", "academic background", "academics",
                "certifications", "certificates", "licenses",
                "projects", "key projects", "notable projects",
                "achievements", "awards", "honors", "publications"
        );

        for (String line : lines) {
            if (line.isBlank()) continue;

            String normalized = line.trim().replaceAll("[:\\-#=]+$", "").trim().toLowerCase();
            if (commonHeadings.contains(normalized) || (line.length() < 35 && line.toUpperCase().equals(line) && line.matches("^[A-Z\\s]{3,}$"))) {
                currentSection = normalized;
                sections.putIfAbsent(currentSection, new ArrayList<>());
            } else {
                sections.computeIfAbsent(currentSection, k -> new ArrayList<>()).add(line);
            }
        }
        return sections;
    }

    private static List<String> getSectionContent(Map<String, List<String>> sections, String... aliases) {
        for (String alias : aliases) {
            for (Map.Entry<String, List<String>> entry : sections.entrySet()) {
                if (entry.getKey().toLowerCase().contains(alias.toLowerCase())) {
                    return entry.getValue();
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<String> extractSkillsList(List<String> lines) {
        List<String> skills = new ArrayList<>();
        for (String line : lines) {
            String clean = line.replaceFirst("^[•\\-*\\d.]+\\s*", "");
            String[] tokens = clean.split("[,|•;\\/]");
            for (String token : tokens) {
                String trimmed = token.trim();
                if (!trimmed.isEmpty() && trimmed.length() < 40 && !trimmed.toLowerCase().startsWith("proficient in")) {
                    skills.add(trimmed);
                }
            }
        }
        return skills;
    }

    private static void categorizeSkills(List<String> allSkills, ResumeContent content) {
        Set<String> techKeywords = Set.of(
                "java", "spring", "spring boot", "python", "javascript", "typescript", "react", "angular", "vue",
                "node", "nodejs", "c++", "c#", ".net", "golang", "go", "rust", "sql", "postgresql", "mysql",
                "mongodb", "redis", "kafka", "rabbitmq", "aws", "azure", "gcp", "docker", "kubernetes", "k8s",
                "terraform", "ci/cd", "git", "linux", "rest", "graphql", "microservices", "hibernate", "jpa"
        );

        Set<String> softKeywords = Set.of(
                "leadership", "communication", "teamwork", "collaboration", "problem solving", "mentoring",
                "agile", "scrum", "adaptability", "critical thinking", "time management", "conflict resolution"
        );

        List<String> tech = new ArrayList<>();
        List<String> soft = new ArrayList<>();

        for (String s : allSkills) {
            String lower = s.toLowerCase();
            if (techKeywords.stream().anyMatch(lower::contains)) {
                tech.add(s);
            } else if (softKeywords.stream().anyMatch(lower::contains)) {
                soft.add(s);
            } else {
                tech.add(s);
            }
        }
        content.setTechnicalSkills(tech);
        content.setSoftSkills(soft);
    }

    private static List<WorkExperience> parseWorkExperience(List<String> lines) {
        List<WorkExperience> list = new ArrayList<>();
        WorkExperience current = null;

        for (String line : lines) {
            boolean isBullet = line.startsWith("•") || line.startsWith("-") || line.startsWith("*") || line.matches("^\\d+\\..*");
            Matcher yearMatcher = YEAR_RANGE_PATTERN.matcher(line);

            if (yearMatcher.find() && !isBullet) {
                if (current != null) {
                    list.add(current);
                }
                current = new WorkExperience();
                current.setStartDate(yearMatcher.group(1));
                current.setEndDate(yearMatcher.group(2));
                current.setCurrent(yearMatcher.group(2).toLowerCase().contains("present") || yearMatcher.group(2).toLowerCase().contains("current"));

                // Attempt to parse title and company from line or prior lines
                String textWithoutDate = line.replaceAll(YEAR_RANGE_PATTERN.pattern(), "").trim();
                String[] parts = textWithoutDate.split("[-|–,@]");
                if (parts.length > 1) {
                    current.setJobTitle(parts[0].trim());
                    current.setCompany(parts[1].trim());
                } else if (!textWithoutDate.isBlank()) {
                    current.setJobTitle(textWithoutDate);
                }
            } else if (isBullet) {
                if (current == null) {
                    current = new WorkExperience();
                    current.setJobTitle("Role");
                }
                String bulletClean = line.replaceFirst("^[•\\-*\\d.]+\\s*", "").trim();
                if (!bulletClean.isBlank()) {
                    current.getBulletPoints().add(bulletClean);
                }
            } else {
                if (current != null && current.getCompany() == null && line.length() < 60) {
                    current.setCompany(line);
                }
            }
        }
        if (current != null) {
            list.add(current);
        }
        return list;
    }

    private static List<EducationItem> parseEducation(List<String> lines) {
        List<EducationItem> educations = new ArrayList<>();
        EducationItem item = null;

        for (String line : lines) {
            String lower = line.toLowerCase();
            boolean hasDegreeWord = lower.contains("bachelor") || lower.contains("master") || lower.contains("phd")
                    || lower.contains("b.s") || lower.contains("m.s") || lower.contains("b.tech") || lower.contains("degree");
            boolean hasUnivWord = lower.contains("university") || lower.contains("college") || lower.contains("institute");

            if (hasDegreeWord || hasUnivWord) {
                if (item != null) educations.add(item);
                item = new EducationItem();
                if (hasDegreeWord) item.setDegree(line);
                if (hasUnivWord) item.setInstitution(line);
            } else if (item != null) {
                if (line.matches(".*(20\\d{2}|19\\d{2}).*")) {
                    item.setGraduationYear(line.replaceAll("[^0-9]", ""));
                } else if (item.getInstitution() == null && line.length() < 80) {
                    item.setInstitution(line);
                }
            }
        }
        if (item != null) educations.add(item);
        return educations;
    }

    private static List<CertificationItem> parseCertifications(List<String> lines) {
        List<CertificationItem> list = new ArrayList<>();
        for (String line : lines) {
            String clean = line.replaceFirst("^[•\\-*\\d.]+\\s*", "").trim();
            if (!clean.isBlank() && clean.length() < 120) {
                CertificationItem cert = new CertificationItem();
                cert.setName(clean);
                list.add(cert);
            }
        }
        return list;
    }

    private static List<ProjectItem> parseProjects(List<String> lines) {
        List<ProjectItem> list = new ArrayList<>();
        ProjectItem current = null;

        for (String line : lines) {
            boolean isBullet = line.startsWith("•") || line.startsWith("-") || line.startsWith("*");
            if (!isBullet && line.length() < 60 && !line.isBlank()) {
                if (current != null) list.add(current);
                current = new ProjectItem();
                current.setName(line);
            } else if (isBullet && current != null) {
                current.getBulletPoints().add(line.replaceFirst("^[•\\-*\\d.]+\\s*", "").trim());
            }
        }
        if (current != null) list.add(current);
        return list;
    }

    private static Double estimateYears(List<WorkExperience> experiences, String rawText) {
        if (!experiences.isEmpty()) {
            int totalYears = 0;
            for (WorkExperience exp : experiences) {
                try {
                    int start = Integer.parseInt(exp.getStartDate());
                    int end = exp.isCurrent() ? 2026 : Integer.parseInt(exp.getEndDate());
                    if (end >= start) {
                        totalYears += (end - start);
                    }
                } catch (Exception ignored) {}
            }
            if (totalYears > 0) return (double) Math.min(totalYears, 30);
        }

        // Regex fallback: "X+ years of experience"
        Pattern p = Pattern.compile("(\\d+)\\+?\\s*(?:years|yrs)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(rawText);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (Exception ignored) {}
        }
        return 3.0; // Default sensible baseline
    }

    private static FormattingInspection inspectFormatting(String rawText, String[] lines, Map<String, List<String>> sections, ContactInfo contact) {
        FormattingInspection f = new FormattingInspection();
        double score = 100.0;

        // Check standard headings
        boolean hasExp = sections.keySet().stream().anyMatch(s -> s.contains("experience") || s.contains("employment"));
        boolean hasEdu = sections.keySet().stream().anyMatch(s -> s.contains("education") || s.contains("academics"));
        boolean hasSkills = sections.keySet().stream().anyMatch(s -> s.contains("skill") || s.contains("competenc") || s.contains("technolog"));

        if (hasExp && hasEdu && hasSkills) {
            f.getPassedChecks().add("Standard ATS section headings detected (Experience, Education, Skills).");
        } else {
            score -= 15.0;
            f.setHasStandardHeadings(false);
            f.getWarningIssues().add("Non-standard or missing primary section headers. ATS parsers rely on standard names.");
        }

        // Contact info check
        if (contact.getEmail() != null && contact.getPhone() != null) {
            f.getPassedChecks().add("Essential contact information (Email and Phone) properly extracted.");
        } else {
            score -= 10.0;
            f.getWarningIssues().add("Missing complete contact details (Email or Phone number could not be reliably extracted).");
        }

        if (contact.getLinkedIn() != null) {
            f.getPassedChecks().add("Professional LinkedIn profile link detected.");
        }

        // Check for table indicators: lines with 3 or more tab stops or pipe columns
        long tableLikeLines = Arrays.stream(lines).filter(l -> l.split("\t").length >= 3 || l.split("\\|").length >= 3).count();
        if (tableLikeLines > 3) {
            score -= 15.0;
            f.setHasTables(true);
            f.getWarningIssues().add("Possible table structure or tabbed column alignments detected. Many older ATS systems scramble table text.");
        } else {
            f.getPassedChecks().add("Clean single-column linear text layout detected.");
        }

        // Check for excessive special characters / icons
        long iconCount = rawText.codePoints().filter(cp -> cp > 0x1F300 && cp < 0x1FAFF).count();
        if (iconCount > 4) {
            score -= 10.0;
            f.getWarningIssues().add("Excessive emojis or decorative icons detected. ATS parsers frequently fail on unicode symbols.");
        }

        // Machine readability
        if (rawText.length() > 200) {
            f.getPassedChecks().add("Resume text is machine-readable without image OCR degradation.");
        } else {
            score -= 30.0;
            f.setTextIsMachineReadable(false);
            f.getCriticalIssues().add("Insufficient machine-readable text detected. Document may be an image-only scan requiring OCR.");
        }

        f.setScore(Math.max(20.0, Math.min(100.0, score)));
        return f;
    }
}
