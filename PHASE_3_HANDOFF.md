# PTMS Modernization — Final Handoff

Phase 3 completes the three-phase modernization. The source is now organized and documented as a final project rather than a transitional phase implementation.

## Phase 3 changes

- Added Spring Data JPA auditing (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`).
- Added a Security-backed `AuditorAware`, falling back to `system` for non-user work.
- Simplified full revision history to `Bus` only.
- Integrated Spring Data Envers `RevisionRepository` for Bus history.
- Added `GET /api/buses/{id}/revisions` as an admin-only DTO endpoint.
- Removed the duplicate `ActionLog`, generic audit repository, reflection diffing, and global audit timeline implementation.
- Added Flyway `V3__auditing_modernization.sql` without rewriting V1/V2, preserving existing migration checksums.
- Removed H2 from tests.
- Rebuilt tests around focused unit tests plus a PostgreSQL/PostGIS Testcontainer integration suite.
- Added coverage for Flyway/Hibernate startup, PostGIS geometry, session login, CSRF, 401/403, validation, 404, 409, Bus revisions, and Actuator health.
- Replaced deprecated `Specification.where(null)` usage.
- Removed obsolete full-stack production-mock configuration and old Phase handoff files.
- Replaced the stale CI workflow with Java 21 + Maven test/package validation.
- Cleaned Docker build configuration.
- Rewrote the README and added architecture/API documentation.
- Added `scripts/verify.ps1` for local final verification.

## Fresh-clone verification on Windows

Requirements:

```text
JDK 21
Docker Desktop running
```

Run:

```powershell
java -version
.\mvnw.cmd -version
.\mvnw.cmd clean test
.\mvnw.cmd clean package
```

Or:

```powershell
.\scripts\verify.ps1
```

The tests use Testcontainers, so Docker must be available.

## Run the application

Start the development PostGIS database:

```powershell
docker compose -f compose.dev.yml up -d
```

Run the backend:

```powershell
.\mvnw.cmd "-Dspring-boot.run.profiles=dev" spring-boot:run
```

Then check:

```text
http://localhost:8080/actuator/health
```

The development-only account is:

```text
username: devadmin
password: Admin123!
```

For API mutation calls, follow the CSRF/session sequence documented in `README.md` and `docs/API.md`.

## Clean database verification

To prove Flyway can build from zero:

```powershell
docker compose -f compose.dev.yml down -v
docker compose -f compose.dev.yml up -d
.\mvnw.cmd "-Dspring-boot.run.profiles=dev" spring-boot:run
```

The application should apply V1 → V2 → V3, validate Hibernate mappings, seed development data, and start normally.

## Production boundary

Production requires an explicit `prod` profile and externally supplied database/CORS secrets. Development sample seeding is disabled in `prod`; no production password is committed to the project.

## Verification performed in the delivery environment

Source-level final checks completed successfully:

```text
Production Java files: 98
Test Java files:        3
Package/path mismatches: 0
Missing internal imports: 0
Java syntax-like parse diagnostics: 0
H2 references: 0
System.out usage: 0
Obsolete ActionLog implementation references: 0
```

I also attempted:

```bash
./mvnw --batch-mode --no-transfer-progress clean test
```

but this generation environment cannot resolve `repo.maven.apache.org`, so the Maven wrapper cannot download Maven/dependencies here. Docker is also not installed in this environment, so the Testcontainers suite cannot be executed here.

For that reason, the authoritative final runtime verification is the Windows command below on a machine with normal internet access and Docker Desktop:

```powershell
.\scripts\verify.ps1
```

That script runs the complete Maven test suite and final package build.
