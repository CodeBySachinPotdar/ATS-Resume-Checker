# PRODUCT.md — Product Vision & Philosophy

## 1. Why Does ResumeMatch AI Exist?

Over 75% of resumes submitted to medium and large enterprises are screened by an Applicant Tracking System (ATS) before ever being read by a human recruiter. Unfortunately, job seekers face major obstacles:

1. **The "Black Box" Barrier**: Job seekers receive generic rejection emails without knowing whether their resume was rejected due to missing technical keywords, poor formatting, or inadequate experience alignment.
2. **The Pitfall of AI "Score Generators"**: Many generic AI tools simply ask ChatGPT to "give this resume a score out of 100". These scores are non-deterministic, vary between prompts, and lack any grounding in how commercial ATS software actually functions.
3. **Harmful AI Advice (Keyword Stuffing & Falsification)**: Naive AI tools often advise candidates to paste entire keyword blocks or claim skills they do not have. Even if such tricks bypass an initial filter, candidates inevitably fail technical interviews.
4. **Formatting Traps**: Candidates spend hours designing attractive two-column resumes with tables, icons, and text boxes—only for ATS parsers to scramble table contents or drop columns completely.

**ResumeMatch AI** solves these challenges by combining:
- **Deterministic ATS parsing & keyword analysis**: Reflecting real ATS entity extraction and keyword heuristics.
- **Strict Anti-Fraud Safeguards**: Encouraging honest, authentic resume presentation.
- **Actionable Achievement Refinement**: Transforming duty-based statements into Google-style quantifiable accomplishments.
- **100% ATS-Safe Export Formats**: Producing single-column resumes guaranteed not to scramble in parsers.

---

## 2. Target Users

| Persona | Core Pain Points | What ResumeMatch AI Delivers |
|---|---|---|
| **Software Engineers (Java, Full-Stack, DevOps, Cloud)** | Diverse tech stacks, multiple synonymous frameworks (e.g. REST APIs vs RESTful Services) | Exact and semantic match detection, required vs nice-to-have skill distinction |
| **Engineering Managers & Tech Leads** | Bullets often read as operational duties rather than business scale | Action verb strengthening, scale metrics, team ownership framing |
| **Career Switchers & Junior Developers** | Unsure how to format project sections to highlight relevant skills | Section scoring, missing mandatory prerequisites detection |
| **Non-Technical Job Seekers** | Overwhelmed by complex ATS specifications | Clean SaaS interface, single-click sample loading, clear plain-English suggestions |

---

## 3. Core Product Principles

### Principle 1: The Anti-Fraud Rule
> **"Never recommend that candidates add skills or experience they do not possess."**

If a job description demands `Kubernetes` and the candidate's resume does not contain it, ResumeMatch AI explicitly advises:
> *"Kubernetes appears to be a core requirement for this role. If you have practical experience with Kubernetes, consider highlighting it in your Technical Skills or recent projects. If you do not have this experience, do not add it merely to game ATS scores."*

Furthermore, bullet point improvements suggest bracketed metric placeholders (e.g. `[reduced latency by X%]`) prompting the user to supply genuine figures rather than hallucinating fake statistics.

### Principle 2: Estimated Compatibility, Not False Guarantees
We never claim: *"Your resume will pass the ATS at Company X."* Instead, we emphasize:
> *"Estimated ATS Compatibility Score based on the supplied job description."*

Different companies configure ATS filters differently. Our score represents a rigorous mathematical proxy of candidate relevance.

### Principle 3: User Control Over AI Suggestions
ResumeMatch AI never automatically overwrites candidate resumes behind the scenes. In **Improvement Mode**, every suggestion displays:
- Original statement
- Identified problem
- Proposed rewrite
- Rationale
- Individual Accept / Reject toggles

---

## 4. Score Tiers & Interpretation

```text
90 - 100   Excellent Match   Exceptional alignment across core technologies and experience
80 - 89    Strong Match      Strong competency; minor keyword or metric opportunities
70 - 79    Good Match        Meets primary requirements but has noticeable gaps in nice-to-haves
60 - 69    Needs Improvement Noticeable gaps in mandatory tools, seniority, or structure
Below 60   Poor Match        Significant misalignment or severe formatting traps
```

---

## 5. Value Proposition Summary

- **Confidence Before Applying**: Identify missing terms and formatting errors prior to job submission.
- **Deterministic Transparency**: Every point of the 100-point score is accounted for across 8 clickable dimensions.
- **Clean SaaS Usability**: Zero complex configurations; instant sample data and 4 ATS-safe export templates.
