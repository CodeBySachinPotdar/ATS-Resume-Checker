# SECURITY.md — Security & Privacy Architecture

## 1. Authentication & Identity Management

ResumeMatch AI implements stateless token-based authentication using **Spring Security 6** and **JJWT 0.12+**.

### Token Specifications
- **Access Token**:
  - Algorithm: HMAC-SHA256 (`HS256`)
  - Expiration: **24 Hours** (86,400,000 ms)
  - Subject: User ID
  - Claims: `email`, `fullName`, `authorities`
- **Refresh Token**:
  - Expiration: **7 Days** (604,800,000 ms)
  - Subject: User ID
  - Claim: `type: "REFRESH"`

---

## 2. Password Policy & Hashing

- **Hashing Algorithm**: BCrypt (via `BCryptPasswordEncoder`) with work factor (strength) 10.
- **Salting**: Secure cryptographic salt automatically generated per password hash.
- **Constraints**: Minimum 6 characters required at registration. Plaintext passwords are never logged or stored.

---

## 3. Authorization & Role-Based Access Control (RBAC)

The application defines two primary role tiers in `com.resumematch.domain.UserRole`:
- `ROLE_USER`: Standard candidate access. Can upload, parse, analyze, improve, and export their own resumes.
- `ROLE_ADMIN`: Administrative visibility across platform metrics and system health.

Method-level security is enabled via `@EnableMethodSecurity`.

### Public vs Protected Endpoints
| URI Pattern | HTTP Methods | Access Level | Description |
|---|---|---|---|
| `/api/v1/auth/**` | POST, GET | `permitAll()` | Registration, login, token refresh |
| `/api/v1/samples/**` | GET | `permitAll()` | Sample resumes & JDs for instant evaluation |
| `/api/v1/analyses/quick-check` | POST | `permitAll()` | Single-click ATS check for guest trial |
| `/api/v1/resumes/export-preview` | POST | `permitAll()` | Instant document download for custom text |
| `/v3/api-docs/**`, `/swagger-ui/**` | GET | `permitAll()` | Interactive API documentation |
| `/actuator/health` | GET | `permitAll()` | Liveness/readiness probes |
| `/api/v1/resumes/**` | GET, POST, DELETE | `authenticated` | Saved user resume management |
| `/api/v1/analyses/**` | GET, POST | `authenticated` | Asynchronous analysis & history |

---

## 4. Cross-Origin Resource Sharing (CORS) & CSRF

- **CORS**: Configured in `SecurityConfig` via `CorsConfigurationSource`.
  - Allowed Origins: Configurable per environment (defaults to `*` with credential support for local Vite development).
  - Allowed Methods: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`.
  - Max Age: 3600 seconds.
- **CSRF**: Disabled (`AbstractHttpConfigurer::disable`) because the application is fully stateless and communicates via `Authorization: Bearer <token>` headers rather than browser session cookies.

---

## 5. File Upload Protection & Anti-Execution

Resumes can be a vector for malicious payloads. The application enforces:
1. **Strict File Size Limits**: 10MB maximum file size, 15MB maximum request size enforced by Spring `StandardServletMultipartResolver`.
2. **Never Execute Uploaded Files**: Files are processed exclusively in-memory as `InputStream` byte streams using Apache PDFBox and Apache POI. Files are never saved to executable disk locations (`/tmp`, `/bin`, or working directories).
3. **MIME & Extension Whitelisting**: Uploads are restricted to `.pdf`, `.docx`, `.doc`, and `.txt`.
4. **Encrypted PDF Handling**: Encrypted or password-protected PDFs are rejected gracefully with a descriptive error rather than hanging JVM worker threads.

---

## 6. Privacy & Data Deletion (GDPR / CCPA)

Candidate resumes contain Personally Identifiable Information (PII) including names, telephone numbers, and email addresses.
- **Zero AI Training Use**: Resumes processed by ResumeMatch AI are never sold, retained for commercial model training, or shared with third parties.
- **Cascade Deletion**: When a user invokes `DELETE /api/v1/resumes/{id}`, all associated parsed versions, evaluations, score breakdowns, and recommendations are permanently deleted via database cascade.
- **Ephemeral Quick Check**: The quick-check trial mode stores minimal data required to compute and display results and can be pruned via scheduled maintenance.
