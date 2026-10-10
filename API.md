# API.md — REST API Documentation

Base URL: `http://localhost:8080/api/v1`  
OpenAPI / Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI JSON Specification: `http://localhost:8080/v3/api-docs`

---

## 1. Authentication & Security

All authenticated endpoints expect an HTTP Header:
```http
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

### Standard Error Response Format
All errors return consistent JSON:
```json
{
  "success": false,
  "errorCode": "RESUME_PARSE_FAILED",
  "message": "The uploaded file contains insufficient readable text.",
  "timestamp": "2026-10-07T14:20:00Z",
  "validationErrors": null
}
```

---

## 2. Authentication Endpoints (`/api/v1/auth`)

### `POST /auth/login`
Authenticate existing user and obtain access + refresh tokens.
```json
// Request
{
  "email": "demo@resumematch.ai",
  "password": "password123"
}

// Response (200 OK)
{
  "token": "eyJhbGciOiJIUzI1NiIsIn...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsIn...",
  "tokenType": "Bearer",
  "userId": 1,
  "email": "demo@resumematch.ai",
  "fullName": "Demo Candidate",
  "role": "ROLE_USER"
}
```

### `POST /auth/register`
Register a new candidate account.
```json
// Request
{
  "email": "alex.chen@example.com",
  "password": "strongPassword123",
  "fullName": "Alexander Chen"
}
```

### `POST /auth/refresh`
Refresh expired JWT access token using valid refresh token.
```json
// Request
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsIn..."
}
```

### `GET /auth/me`
Retrieve currently authenticated user profile.

---

## 3. Resume Management Endpoints (`/api/v1/resumes`)

### `POST /resumes/upload`
Upload a resume file (`multipart/form-data`). Supported extensions: `.pdf`, `.docx`, `.doc`, `.txt`.
- **Parameter**: `file` (Multipart file, max 10MB)
```json
// Response (200 OK)
{
  "id": 1,
  "title": "Senior_Java_Developer_Resume",
  "fileName": "Senior_Java_Developer_Resume.pdf",
  "fileType": "PDF",
  "fileSize": 142850,
  "candidateName": "Alexander Chen",
  "candidateEmail": "alex.chen.dev@example.com",
  "candidatePhone": "(415) 555-0192",
  "rawText": "ALEXANDER CHEN\nSan Francisco...",
  "createdAt": "2026-10-07T14:20:00Z"
}
```

### `POST /resumes/text`
Paste resume content as raw text.
```json
// Request
{
  "title": "My Software Engineer Resume",
  "text": "ALEXANDER CHEN\nSan Francisco, CA\n...\nWORK EXPERIENCE\n..."
}
```

### `GET /resumes`
List all saved resumes for the current authenticated user.

### `GET /resumes/{id}`
Retrieve resume details by ID.

### `DELETE /resumes/{id}`
Permanently delete resume and associated versions.

---

## 4. Job Description Endpoints (`/api/v1/job-descriptions`)

### `POST /job-descriptions`
Submit a target job description.
```json
// Request
{
  "title": "Senior Java Developer",
  "company": "ABC Global FinTech",
  "location": "San Francisco, CA",
  "text": "We are seeking a Senior Java Developer with 5+ years experience in Spring Boot, Kafka, and Kubernetes..."
}
```

### `GET /job-descriptions/{id}`
Get parsed job description requirements.

---

## 5. ATS Analysis Endpoints (`/api/v1/analyses`)

### `POST /analyses/quick-check`
**Instant Synchronous ATS Analysis**: Evaluates resume text against job description text immediately. Ideal for interactive demos and guest users.
```json
// Request
{
  "resumeTitle": "Alexander Chen - Senior Java Dev",
  "resumeText": "...",
  "jobTitle": "Senior Java Developer",
  "company": "ABC Global FinTech",
  "jobDescriptionText": "..."
}

// Response (200 OK)
{
  "id": 1,
  "resumeId": 1,
  "resumeTitle": "Alexander Chen - Senior Java Dev",
  "jobDescriptionId": 1,
  "jobTitle": "Senior Java Developer",
  "company": "ABC Global FinTech",
  "overallScore": 82.0,
  "scoreCategory": "STRONG_MATCH",
  "status": "COMPLETED",
  "formattingScore": 95.0,
  "processingDurationMs": 142,
  "scoreBreakdown": {
    "keywordMatchScore": 88.0,
    "requiredSkillsScore": 90.0,
    "experienceAlignmentScore": 78.0,
    "jobTitleAlignmentScore": 85.0,
    "semanticSimilarityScore": 81.0,
    "resumeStructureScore": 95.0,
    "achievementQualityScore": 72.0,
    "formattingCompatibilityScore": 95.0,
    "breakdownDetails": { ... }
  },
  "keywordMatches": [
    {
      "keyword": "Java",
      "matchStatus": "MATCHED",
      "skillCategory": "TECHNICAL",
      "priorityLevel": "HIGH",
      "isMustHave": true
    },
    {
      "keyword": "RESTful Web Services",
      "matchStatus": "RELATED",
      "skillCategory": "TECHNICAL",
      "relatedTerm": "REST APIs",
      "priorityLevel": "HIGH"
    },
    {
      "keyword": "Kubernetes",
      "matchStatus": "MISSING",
      "skillCategory": "DEVOPS_CLOUD",
      "priorityLevel": "CRITICAL",
      "isMustHave": true
    }
  ],
  "recommendations": [ ... ],
  "topStrengths": [
    "✓ Java experience strongly matches",
    "✓ Spring Boot clearly demonstrated",
    "✓ Microservices experience detected"
  ],
  "criticalGaps": [
    "⚠ Kubernetes not detected in your resume.",
    "⚠ Several experience bullets lack measurable impact."
  ]
}
```

### `POST /analyses`
**Asynchronous Analysis (Kafka Event)**: Enqueues a job for background processing. Returns status `PENDING`.
```json
// Request
{
  "resumeId": 1,
  "jobDescriptionId": 1
}
```

### `GET /analyses/{id}`
Retrieve full analysis report and current processing status.

### `GET /analyses/{id}/score`
Retrieve only the 8-category score breakdown.

### `GET /analyses/{id}/keywords`
Retrieve matched, related, and missing keyword items.

### `GET /analyses/{id}/recommendations`
Retrieve prioritized actionable recommendations.

---

## 6. Optimization & Export Endpoints (`/api/v1/resumes`)

### `POST /resumes/{id}/improvements?targetRole=Senior+Java+Developer`
Fetch structured bullet improvements (`Original` → `Problem` → `Improved` → `Why better`) and summary recommendations.

### `POST /resumes/{id}/optimize`
Apply accepted suggestions and save a new version of the resume.

### `GET /resumes/{id}/export?format={pdf|docx|txt}&template={CLASSIC_PROFESSIONAL|MODERN_MINIMAL|SOFTWARE_ENGINEER|EXECUTIVE}`
Stream downloaded ATS-safe document file.

### `POST /resumes/export-preview?format={pdf|docx|txt}&template={CLASSIC_PROFESSIONAL}`
Generate on-the-fly download from custom text body without persisting.
