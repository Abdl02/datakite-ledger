# AI Development Log — datakite-ledger

## 1. AI Tooling Stack

| Tool | Detail |
|------|--------|
| **IDE** | Cursor IDE |
| **AI Model** | Claude Sonnet 4.6 (via Cursor's agent interface) |
| **Usage** | Full-stack code generation, debugging assistance, documentation authoring |

---

## 2. Prompt Engineering Strategy

The project was built using a **modular, step-by-step prompting approach**, deliberately separating each layer of the stack into its own prompting phase. This prevented context overload and allowed targeted human validation at each stage before proceeding.

### Phase 1 — Core Backend

The first prompt focused exclusively on the Spring Boot backend skeleton:

- Domain entity (`Transaction`) with JPA annotations and Lombok
- Repository layer (`TransactionRepository`) extending `JpaRepository`
- Service interface and implementation (`TransactionServiceImpl`)
- REST controller (`TransactionController`) with full CRUD + filtering endpoints
- DTOs, MapStruct mapper, validation annotations, and global exception handler
- `application.yml` wired to a local PostgreSQL database (`ledger_db` on port 5432)
- Maven `pom.xml` with all required dependencies (Spring Web, Data JPA, Validation, Actuator, PostgreSQL, Lombok, MapStruct)

This phase was scoped deliberately to the backend only, so that the generated code could be compiled and manually tested before any frontend work began.

### Phase 2 — Caching Wrapper + Interceptor

With the core backend stable, a second prompt introduced the cross-cutting infrastructure:

- An HTTP request/response logging interceptor registered via `WebMvcConfigurer`
- JSON serialization safety (handling malformed payloads in the interceptor)
- Spring Actuator exposure configuration (`health`, `info`, `metrics` endpoints)

Keeping this as a separate phase meant the interceptor could be reviewed and tested in isolation without touching the domain logic.

### Phase 3 — Next.js Frontend

Only after the backend API was verified did a third prompt generate the frontend:

- Next.js 16 app-router project with TypeScript and Tailwind CSS 4
- Dashboard page consuming the backend REST API (`http://localhost:8080/api/transactions`)
- shadcn/ui components (Table, Badge, Card, Input, Select, Button)
- Recharts integration for a summary bar chart
- Environment variable (`NEXT_PUBLIC_API_URL`) wired via `.env.local`

---

## 3. Human-in-the-Loop Validation

### Critical Bug: Hidden Null Byte in `pom.xml`

During Phase 1 (Core Backend generation), a subtle but critical flaw was introduced by the AI. The generated `pom.xml` contained a **hidden null byte (`\u0000`) prepended before the XML declaration** (`<?xml version="1.0" encoding="UTF-8"?>`). Because the character is non-printable, it was invisible in most text editors.

**Symptom:** Running `mvn spring-boot:run` immediately after generation produced a Maven parsing error:

```
[ERROR] Failed to read artifact descriptor for ...: Could not parse pom.xml
[ERROR] Error parsing XML: Content not allowed in prolog.
```

The error message "Content not allowed in prolog" is the canonical Maven/SAX parser signal that a byte-order mark or non-XML character precedes the XML declaration.

**Identification:** The error was caught by inspecting the terminal output immediately after the first build attempt — part of the standard human validation step taken between each prompting phase. The raw file was examined to confirm the presence of the null byte at byte offset 0.

**Resolution:** The AI was explicitly prompted to **rewrite `pom.xml` from scratch with clean UTF-8 encoding**, with the instruction to ensure no BOM or non-printable characters were present. The regenerated file parsed successfully, and `mvn spring-boot:run` completed without errors on the subsequent attempt.

**Lesson documented:** AI code generators can silently introduce encoding artifacts when composing XML files. Validating build files with an actual compile step before moving to the next phase is essential.

---

## 4. Security Checks & Linter Fixes

During Phase 2 (Interceptor development), the AI's **internal linter** flagged a class of unhandled exceptions in the HTTP interceptor:

- **Issue:** The interceptor read and attempted to parse the raw request/response body as JSON without a surrounding try-catch. Any malformed or non-JSON payload (e.g., a binary file upload or a truncated request) would cause an unhandled `JsonParseException` to propagate, resulting in a 500 error rather than graceful logging.
- **Detection:** The linter surfaced this as a warning during the same generation turn; no separate audit pass was required.
- **Fix:** The AI automatically wrapped the JSON parsing block in a `try-catch`, logging a warning and falling back to raw string logging when parsing failed. The human code reviewer inspected the diff, confirmed the fix was correct and did not alter the interceptor's primary logging behaviour, and approved the change.

This represents a clean example of the intended human-in-the-loop workflow: AI proposes an automatic fix, human reviews and approves before the code is accepted into the working branch.
