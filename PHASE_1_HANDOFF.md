# PTMS Modernization - Phase 1 Handoff

This repository is at **Phase 1 of 3** of the PTMS modernization. Phase 1 establishes the application foundation; it is intentionally **not** the final API/security/auditing/test design.

## What Phase 1 changed

- Shortened the base package to `com.tritonptms.ptms` and moved `PtmsApplication` to that package root.
- Reorganized production code by feature (`bus`, `route`, `employee`, `schedule`, `assignment`, `user`, `auth`, `security`, `dashboard`, `audit`) while retaining normal controller/service/repository layers inside each feature.
- Removed the custom `ApplicationContextHelper` pattern and standardized constructor injection.
- Removed application service interfaces that only had one implementation. `UserDetailsService` remains because it is a Spring Security framework contract.
- Replaced scattered application settings with typed `AppProperties` configuration.
- Removed the implicit `dev` profile fallback. A runtime profile must now be selected explicitly.
- Added explicit `dev`, `test`, and `prod` configuration files.
- Added Flyway migrations and changed normal runtime Hibernate schema behavior to `ddl-auto=validate`.
- Simplified `Vehicle -> Bus`; `Bus` is now a normal entity because there was no other useful vehicle subtype.
- Cleaned entity table/column/index/relationship mappings and moved repositories/specifications next to their features.
- Added service-level read/write transaction boundaries.
- Replaced the collection of startup data loaders with one development-only `DevDataSeeder`.
- Moved essential roles to Flyway reference-data migration.
- Removed the Hypersistence JSON dependency in favor of Hibernate's native JSON mapping for action-log changes.
- Repaired the Unix Maven wrapper launcher and retained the Windows wrapper.
- Added `compose.dev.yml` for a local PostgreSQL + PostGIS development database.
- Updated the development PowerShell launcher and Docker build/entrypoint so profiles are explicit.

## Deliberately deferred to Phase 2 / Phase 3

Do not treat the following as Phase 1 defects unless they block the Phase 1 foundation:

- request/response DTO redesign and manual mapping cleanup;
- controller/API contract consistency;
- validation redesign;
- Spring Security/login/CSRF/method-authorization redesign;
- final exception/error response format;
- audit/Envers redesign;
- migration and repair of the existing test suite;
- PostgreSQL/PostGIS integration tests;
- final README/API documentation and final end-to-end cleanup.

The existing Envers-based audit API remains in the source tree, but Envers event integration is disabled for Phase 1. Auditing is intentionally revisited in Phase 3.

The pre-existing tests still target the old package/layout and are intentionally left for Phase 3. Therefore, **do not use `mvn test` as the Phase 1 acceptance command**. Compile/package the production application with tests skipped as shown below.

## Fresh-clone setup on Windows

### Prerequisites

Install these once on the machine:

- **JDK 21**
- **Docker Desktop** (for the provided PostgreSQL/PostGIS development database)
- Git

You do **not** need to install Maven or Spring Boot globally. The Maven wrapper (`mvnw.cmd`) downloads the configured Maven version and all project dependencies automatically on first use.

From PowerShell in the cloned project directory:

```powershell
java -version

# First invocation downloads Maven 3.9.11.
.\mvnw.cmd -version

# Maven now downloads the Spring Boot/project dependencies and compiles production code.
.\mvnw.cmd clean compile

# Build the runnable JAR without compiling/running the deferred old tests.
.\mvnw.cmd -Dmaven.test.skip=true clean package

# Docker is only required when you want to run the app with the provided dev database.
docker --version
docker compose version
docker compose -f compose.dev.yml up -d

# Run with the development profile. Flyway creates/updates the schema automatically.
java -jar target\ptms-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Alternatively, run directly through the Spring Boot Maven plugin:

```powershell
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run -Dspring-boot.run.profiles=dev
```

### Optional `.env.dev` launcher

The repository includes `.env.example` and `scripts/run-dev.ps1`:

```powershell
Copy-Item .env.example .env.dev
.\scripts\run-dev.ps1
```

`.env.dev` is ignored by Git.

### Development sample login

When `dev` is active and `APP_SEED_ENABLED=true`, the development seeder creates this account if it does not already exist:

```text
username: devadmin
password: Admin123!
```

This credential is development-only. Production explicitly disables the sample seeder.

To run `dev` without sample data for the current PowerShell session:

```powershell
$env:APP_SEED_ENABLED="false"
.\mvnw.cmd -Dmaven.test.skip=true spring-boot:run -Dspring-boot.run.profiles=dev
```

### Stop or reset the development database

Stop it while keeping the database volume:

```powershell
docker compose -f compose.dev.yml down
```

Delete the development database volume and recreate a completely clean database:

```powershell
docker compose -f compose.dev.yml down -v
docker compose -f compose.dev.yml up -d
```

The next application startup runs Flyway from an empty database.

## Linux/macOS equivalent

```bash
chmod +x mvnw
./mvnw -version
./mvnw clean compile
./mvnw -Dmaven.test.skip=true clean package
docker compose -f compose.dev.yml up -d
java -jar target/ptms-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## Profile behavior

There is no default `dev` activation anymore.

- `dev`: local development settings and optional sample seeding.
- `test`: transitional Phase 1 test configuration; full integration-test modernization is Phase 3.
- `prod`: requires database/CORS settings from the environment and never enables the sample seeder.

Example production-style environment values are intentionally **not** committed as real secrets.

## Schema ownership

Normal runtime schema ownership is now:

```text
Flyway -> creates/version-controls PostgreSQL/PostGIS schema
Hibernate/JPA -> validates mappings and performs ORM operations
```

Migration files are under:

```text
src/main/resources/db/migration/
```

`V1__initial_schema.sql` creates the clean Phase 1 schema and enables PostGIS. `V2__essential_reference_data.sql` creates the essential application roles.

## Verification note

The project was structurally/static-checked after the refactor for package/path consistency, internal imports, removed service-locator/field-injection patterns, service-interface leftovers, unsafe `dev` profile fallback, and `ddl-auto=update` leftovers.

A full Maven dependency compile could not be executed in the editing environment because that environment has no installed Maven cache and cannot resolve `repo.maven.apache.org`. The repaired wrapper reaches the Maven download step correctly. Run the Windows commands above on a normal networked development machine; if the production-source compile reports an error, use that compiler output as the next handoff input before starting Phase 2.
