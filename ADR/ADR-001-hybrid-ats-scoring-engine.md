# ADR-001: Hybrid Deterministic and Semantic ATS Scoring Engine

## Status
Accepted

## Context
Commercial Applicant Tracking Systems (ATS) like Workday, Greenhouse, Taleo, and Lever do not rely solely on LLMs to rank candidates. Instead, they extract entities, build search indexes, and compute exact/stemmed keyword matching along with role seniority criteria. Relying purely on an LLM for ATS scoring has severe drawbacks:
1. **Non-deterministic scoring**: The same resume and job description could yield different scores on subsequent runs.
2. **Hallucination risk**: LLMs can invent matches or miscalculate mathematical weights.
3. **High API cost & latency**: Full-document LLM evaluations are slow (3-10s) and costly.

## Decision
We implemented a **hybrid scoring engine** combining deterministic mathematical models with semantic embedding similarity:
- 8 Configurable Category Weights:
  - Keyword Match: 25%
  - Required Skills (Must-Have): 20%
  - Experience Alignment: 15%
  - Job Title Alignment: 10%
  - Semantic Similarity: 10%
  - Resume Structure: 10%
  - Achievement Quality: 5%
  - Formatting Compatibility: 5%
- Deterministic extraction is executed first for exact and synoptic keyword matches.
- Vector cosine similarity calculates conceptual alignment between resume experience and JD responsibilities.
- LLMs are reserved for semantic reasoning: actionable recommendations, bullet rewrites, and summary polishing.

## Consequences
- **Positive**: 100% reproducible, predictable scores; transparent clickable breakdown; sub-second scoring engine; lower LLM operating costs.
- **Negative**: Requires maintaining and expanding keyword dictionaries and semantic synonym graphs.
