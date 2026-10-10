# ADR-006: JWT Stateless Security Architecture

## Status
Accepted

## Context
Resume analysis tools need to support both authenticated SaaS accounts (with saved history, quotas, and versions) and instant guest/trial users without authentication hurdles.

## Decision
We implemented a stateless security model with Spring Security 6 & JJWT 0.12+:
- **Stateless Sessions**: `SessionCreationPolicy.STATELESS` ensures horizontal scalability without server-side sticky sessions.
- **Dual Tokens**: 24-hour access JWTs paired with 7-day secure refresh tokens.
- **BCrypt Password Hashing**: Passwords hashed using BCrypt.
- **Public Endpoints**: `/api/v1/auth/**`, `/api/v1/samples/**`, and `/api/v1/analyses/quick-check` permit unauthenticated guest trials, enabling immediate user engagement.
- **Authenticated Endpoints**: User dashboard, historical analyses, and resume versions require valid Bearer JWT.

## Consequences
- **Positive**: Zero session memory footprint on backend; smooth mobile/web cross-platform consumption; unblocked guest demo experience.
- **Negative**: Token revocation requires short lifetimes or a Redis token blocklist.
