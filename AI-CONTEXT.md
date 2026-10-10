# AI-CONTEXT.md — Instruction Manual for AI Agents

> **CRITICAL DIRECTIVE FOR AI AGENTS**: Read this file before making modifications to the ResumeMatch AI codebase. This document outlines architectural invariants, anti-fraud guardrails, and coding patterns required to maintain the integrity of this repository.

---

## 1. Core Architectural Invariants

1. **Do NOT Replace the Deterministic Scoring Engine with an LLM Prompt**:
   - The ATS score is calculated by `DefaultAtsScoringEngine` using a weighted formula across 8 explicit categories.
   - LLMs (`AIProvider`) are strictly reserved for qualitative reasoning: bullet point improvement, summary rewriting, and generating helpful recommendations.
   - Never remove or bypass the deterministic calculation in favor of a single `"ask LLM for a score out of 100"` prompt.

2. **The Anti-Fraud Principle is Non-Negotiable**:
   - When suggesting keywords or bullet points, the AI must never fabricate credentials, employment dates, or metrics.
   - If recommending an absent technology (e.g., Kubernetes), always include the anti-fraud disclaimer reminding the candidate to add it only if they have authentic experience.
   - Bullet improvements must use metric placeholders (e.g., `[improved latency by X%]`) when authentic numbers are absent.

3. **No Business Logic in Controllers**:
   - Spring Boot controllers (`com.resumematch.controller.*`) are strictly HTTP transport adapters.
   - All validation, scoring, parsing, and persistence coordination belongs in `com.resumematch.service.*` or `com.resumematch.ats.*`.

4. **Zero Forced External Dependencies for Local Runs**:
   - The application must always start and run out-of-the-box using embedded H2 and `MockAIProvider` when PostgreSQL, Redis, or Kafka are not active.
   - Kafka publishing in `AnalysisEventPublisher` must always retain its in-process fallback to `AsyncAnalysisWorker`.

---

## 2. Directory & Package Map

```text
com.resumematch
 ├── config/       # SecurityConfig, AtsProperties, OpenApiConfig
 ├── controller/   # REST Controllers (Auth, Resume, Analysis, Improvement, Export)
 ├── domain/       # Enums (AtsCategory, MatchStatus, PriorityLevel, TemplateType)
 ├── entity/       # JPA Entities (User, Resume, JobDescription, ResumeAnalysis, etc.)
 ├── repository/   # Spring Data JPA interfaces
 ├── service/      # Application services (AnalysisService, ResumeService, AsyncAnalysisWorker)
 ├── parser/       # Multi-parser factory (PdfResumeParser, DocxResumeParser, TxtResumeParser)
 ├── ats/          # Scoring algorithms, keyword engine, semantic similarity
 ├── ai/           # AIProvider interface, MockAIProvider, OpenAIProvider
 ├── export/       # Document generators (PDFBox, POI XWPF, TXT)
 ├── kafka/        # Event publisher, consumer, and job DTO
 └── exception/    # RestControllerAdvice and custom runtime exceptions
```

---

## 3. How to Extend Key Capabilities

### Adding a New AI Provider
1. Implement `com.resumematch.ai.provider.AIProvider`:
   ```java
   public class AnthropicProvider implements AIProvider {
       @Override public String getProviderName() { return "claude"; }
       // implement analyzeAndImproveBullets, improveSummary, generateRecommendations...
   }
   ```
2. Annotate with `@Component`. `AIProviderFactory` will automatically register and resolve it via `app.ai.provider=claude`.

### Adjusting Scoring Weights
Weights are externalized in `application.yml`:
```yaml
app:
  scoring:
    weights:
      keyword-match: 0.25
      required-skills: 0.20
      experience-alignment: 0.15
      job-title-alignment: 0.10
      semantic-similarity: 0.10
      resume-structure: 0.10
      achievement-quality: 0.05
      formatting-compatibility: 0.05
```

### Adding New ATS Export Templates
1. Register the new template in `com.resumematch.domain.TemplateType`.
2. Add styling and section ordering logic to `PdfExportGenerator` and `DocxExportGenerator`.
3. Update `TemplateSelector` in the frontend `ExportPage.tsx`.

---

## 4. Verification Workflow

When modifying code:
1. Run backend unit tests:
   ```bash
   cd backend && mvn test
   ```
2. Verify frontend compilation and type safety:
   ```bash
   cd frontend && npm run build
   ```
3. Test end-to-end evaluation flow using the sample Java dataset via `/api/v1/analyses/quick-check`.
