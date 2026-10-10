package com.resumematch.ai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Component
public class OpenAIProvider implements AIProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAIProvider.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockAIProvider fallbackProvider = new MockAIProvider();

    @Value("${app.ai.openai.api-key:}")
    private String apiKey;

    @Value("${app.ai.openai.model:gpt-4o-mini}")
    private String model;

    @Override
    public String getProviderName() {
        return "openai";
    }

    private boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.equals("mock") && apiKey.startsWith("sk-");
    }

    @Override
    public List<BulletImprovementDto> analyzeAndImproveBullets(List<String> bullets, String targetRole, List<String> targetKeywords) {
        if (!isConfigured()) {
            log.info("OpenAI API key not configured or mock mode enabled. Using MockAIProvider for bullet improvement.");
            return fallbackProvider.analyzeAndImproveBullets(bullets, targetRole, targetKeywords);
        }

        try {
            String prompt = String.format("""
                You are an expert ATS resume coach.
                Target Role: %s
                Target Keywords: %s
                Analyze these resume bullet points. For each bullet, provide:
                - problem: what is weak (e.g. passive verb, lack of metrics, generic)
                - improved: strong rewritten version starting with an action verb, incorporating relevant tech, suggesting metrics without inventing false facts
                - whyBetter: explanation
                
                Bullets to analyze:
                %s
                
                Return JSON only in this format:
                [
                  {"original": "...", "problem": "...", "improved": "...", "whyBetter": "...", "section": "EXPERIENCE"}
                ]
                """, targetRole, String.join(", ", targetKeywords), String.join("\n- ", bullets));

            String responseText = callOpenAiChat(prompt);
            JsonNode arrayNode = objectMapper.readTree(cleanJsonMarkdown(responseText));
            List<BulletImprovementDto> list = new ArrayList<>();
            for (JsonNode node : arrayNode) {
                list.add(new BulletImprovementDto(
                        node.path("original").asText(),
                        node.path("problem").asText(),
                        node.path("improved").asText(),
                        node.path("whyBetter").asText(),
                        node.path("section").asText("EXPERIENCE")
                ));
            }
            return list;
        } catch (Exception e) {
            log.warn("OpenAI call failed for bullet analysis ({}); falling back to mock provider.", e.getMessage());
            return fallbackProvider.analyzeAndImproveBullets(bullets, targetRole, targetKeywords);
        }
    }

    @Override
    public SummaryImprovementDto improveSummary(String currentSummary, String targetRole, List<String> keySkills) {
        if (!isConfigured()) {
            return fallbackProvider.improveSummary(currentSummary, targetRole, keySkills);
        }

        try {
            String prompt = String.format("""
                Rewrite this professional resume summary for ATS optimization.
                Target Role: %s
                Key Skills: %s
                Current Summary: %s
                
                Return JSON only:
                {
                  "improvedSummary": "...",
                  "rationale": "..."
                }
                """, targetRole, String.join(", ", keySkills), currentSummary);

            String responseText = callOpenAiChat(prompt);
            JsonNode root = objectMapper.readTree(cleanJsonMarkdown(responseText));
            return new SummaryImprovementDto(
                    currentSummary,
                    root.path("improvedSummary").asText(),
                    root.path("rationale").asText(),
                    targetRole
            );
        } catch (Exception e) {
            log.warn("OpenAI call failed for summary improvement; using fallback. Error: {}", e.getMessage());
            return fallbackProvider.improveSummary(currentSummary, targetRole, keySkills);
        }
    }

    @Override
    public List<AiRecommendationDto> generateRecommendations(String resumeText, String jdText, List<String> missingKeywords) {
        if (!isConfigured()) {
            return fallbackProvider.generateRecommendations(resumeText, jdText, missingKeywords);
        }

        try {
            String prompt = String.format("""
                You are an ATS optimization specialist.
                Missing JD Keywords: %s
                Generate actionable recommendations for the candidate.
                IMPORTANT ANTI-FRAUD RULE: Never instruct the candidate to fabricate skills or metrics they do not have.
                Always include an anti-fraud note reminding them to add keywords only if they have authentic experience.
                
                Return JSON only:
                [
                  {
                    "priority": "HIGH",
                    "category": "SKILLS",
                    "title": "...",
                    "description": "...",
                    "antiFraudNote": "..."
                  }
                ]
                """, String.join(", ", missingKeywords));

            String responseText = callOpenAiChat(prompt);
            JsonNode arrayNode = objectMapper.readTree(cleanJsonMarkdown(responseText));
            List<AiRecommendationDto> list = new ArrayList<>();
            for (JsonNode n : arrayNode) {
                list.add(new AiRecommendationDto(
                        com.resumematch.domain.PriorityLevel.valueOf(n.path("priority").asText("MEDIUM")),
                        n.path("category").asText("SKILLS"),
                        n.path("title").asText(),
                        n.path("description").asText(),
                        n.path("antiFraudNote").asText()
                ));
            }
            return list;
        } catch (Exception e) {
            log.warn("OpenAI call failed for recommendations; using fallback. Error: {}", e.getMessage());
            return fallbackProvider.generateRecommendations(resumeText, jdText, missingKeywords);
        }
    }

    @Override
    public String extractJobRequirementsJson(String jdText) {
        return fallbackProvider.extractJobRequirementsJson(jdText);
    }

    @Override
    public String extractResumeStructureJson(String resumeText) {
        return fallbackProvider.extractResumeStructureJson(resumeText);
    }

    private String callOpenAiChat(String systemOrUserPrompt) throws Exception {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", model);
        bodyMap.put("messages", List.of(
                Map.of("role", "system", "content", "You are an ATS analysis assistant. Output valid JSON only."),
                Map.of("role", "user", "content", systemOrUserPrompt)
        ));
        bodyMap.put("temperature", 0.2);

        String jsonBody = objectMapper.writeValueAsString(bodyMap);

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI API returned status: " + response.statusCode());
        }

        JsonNode responseNode = objectMapper.readTree(response.body());
        return responseNode.path("choices").get(0).path("message").path("content").asText();
    }

    private String cleanJsonMarkdown(String raw) {
        if (raw == null) return "{}";
        String clean = raw.trim();
        if (clean.startsWith("```json")) {
            clean = clean.substring(7);
        } else if (clean.startsWith("```")) {
            clean = clean.substring(3);
        }
        if (clean.endsWith("```")) {
            clean = clean.substring(0, clean.length() - 3);
        }
        return clean.trim();
    }
}
