# ADR-005: Dual Database Persistence & Semantic Vector Strategy

## Status
Accepted

## Context
Production workloads require relational durability (PostgreSQL) and semantic similarity search, while local development and rapid test suites benefit from zero-configuration embedded storage (H2).

## Decision
- **Production**: PostgreSQL 16 with Spring Data JPA. Supports relational relationships across Users, Resumes, Analyses, ScoreBreakdowns, and KeywordMatches.
- **Development/Test**: Embedded H2 database configured in PostgreSQL compatibility mode with automatic DDL updates.
- **Semantic Vector Similarity**: Implemented via calibrated in-memory cosine vector service (`SemanticSimilarityService`) on normalized TF-IDF term vectors, with pluggable extension points for PostgreSQL pgvector extension for large-scale enterprise vector indexes.

## Consequences
- **Positive**: Instant out-of-the-box local startup without requiring external DB containers; strict type safety through Hibernate; smooth migration to production Postgres.
- **Negative**: Embedded H2 does not persist data across process restarts unless configured with file-based storage.
