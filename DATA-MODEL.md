# DATA-MODEL.md — Data Model & Entity Specifications

## 1. Entity Relationship (ER) Diagram

```mermaid
erDiagram
    User ||--o{ Resume : "owns (1:N)"
    User ||--o{ JobDescription : "owns (1:N)"
    Resume ||--o{ ResumeVersion : "has versions (1:N)"
    Resume ||--o{ ResumeAnalysis : "evaluated in (1:N)"
    JobDescription ||--o{ ResumeAnalysis : "target in (1:N)"
    ResumeAnalysis ||--|| ScoreBreakdown : "details (1:1)"
    ResumeAnalysis ||--o{ KeywordMatch : "contains (1:N)"
    ResumeAnalysis ||--o{ Recommendation : "suggests (1:N)"
    User ||--o{ AnalysisHistory : "records (1:N)"

    User {
        bigint id PK
        string email UK
        string password
        string fullName
        string role
        integer analysesQuota
        timestamp createdAt
        timestamp updatedAt
    }

    Resume {
        bigint id PK
        bigint user_id FK
        string title
        string fileName
        string fileType
        bigint fileSize
        text rawText
        text parsedJson
        string candidateName
        string candidateEmail
        string candidatePhone
        timestamp createdAt
        timestamp updatedAt
    }

    JobDescription {
        bigint id PK
        bigint user_id FK
        string title
        string company
        string location
        string employmentType
        string experienceRequirement
        text rawText
        text extractedJson
        timestamp createdAt
    }

    ResumeAnalysis {
        bigint id PK
        bigint resume_id FK
        bigint job_description_id FK
        double overallScore
        string scoreCategory
        string status
        double formattingScore
        text formattingIssuesJson
        text strengthsJson
        text gapsJson
        text sectionScoresJson
        bigint processingDurationMs
        timestamp createdAt
    }

    ScoreBreakdown {
        bigint id PK
        bigint analysis_id FK,UK
        double keywordMatchScore
        double requiredSkillsScore
        double experienceAlignmentScore
        double jobTitleAlignmentScore
        double semanticSimilarityScore
        double resumeStructureScore
        double achievementQualityScore
        double formattingCompatibilityScore
        text breakdownDetailsJson
    }

    KeywordMatch {
        bigint id PK
        bigint analysis_id FK
        string keyword
        string matchStatus
        string skillCategory
        string priorityLevel
        string relatedTerm
        string importanceExplanation
        boolean isMustHave
    }

    Recommendation {
        bigint id PK
        bigint analysis_id FK
        string priorityLevel
        string sectionTarget
        string title
        text description
        text antiFraudNote
        text originalBullet
        text problemIdentified
        text improvedBullet
        text whyBetter
        boolean isApplied
    }

    ResumeVersion {
        bigint id PK
        bigint resume_id FK
        integer versionNumber
        string versionName
        text contentText
        text contentJson
        timestamp createdAt
    }

    AnalysisHistory {
        bigint id PK
        bigint userId
        bigint analysisId
        string resumeTitle
        string jobTitle
        string companyName
        double overallScore
        timestamp recordedAt
    }
```

---

## 2. Data Ownership & Bounded Contexts

| Entity | Primary Context Owner | Persistence Lifecycle | Deletion Policy |
|---|---|---|---|
| `User` | Identity & Auth | Master account record | Deleting User cascades to all owned resumes, analyses, and history. |
| `Resume` | Candidate Document Domain | Mutable per candidate, versioned | Deleting Resume cascades to all `ResumeVersion` and `ResumeAnalysis` records. |
| `ResumeVersion` | Resume Builder & Optimization | Immutable historical snapshots | Retained for candidate audit trail until parent resume is deleted. |
| `JobDescription` | Requisition Domain | Immutable job posting specification | Reusable across multiple candidate resume evaluations. |
| `ResumeAnalysis` | ATS Scoring Engine | Immutable evaluation outcome | Cascade deletes `ScoreBreakdown`, `KeywordMatch`, and `Recommendation`. |
| `AnalysisHistory` | Analytics & Audit Context | Lightweight denormalized audit record | Retained for quota and historical reporting. |

---

## 3. Storage Optimization & Indexing Strategy

1. **Unique Indexes**:
   - `users(email)`: Ensures single account per email address.
2. **Foreign Key Indexes**:
   - `resumes(user_id)`: Speeds up dashboard retrieval of candidate resumes.
   - `resume_analyses(resume_id, job_description_id)`: Accelerates evaluation lookups.
   - `keyword_matches(analysis_id)` & `recommendations(analysis_id)`: Quick joins for analysis rendering.
3. **Large Text Field (LOB) Storage**:
   - Resumes and Job Descriptions store raw text in PostgreSQL `TEXT` columns (out-of-line TOAST storage) to maintain compact primary index pages.
4. **Data Retention & Privacy Enforcement**:
   - User account deletion triggers strict cascading cleanup across `resumes` and `analyses`, ensuring full GDPR / CCPA right-to-be-forgotten compliance.
