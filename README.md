# Public Transport Management System (PTMS) Backend

PTMS is a Spring Boot backend for a small public-transport operations domain. The project demonstrates a feature-oriented layered monolith, REST APIs, session-based Spring Security, PostgreSQL/PostGIS persistence, Flyway migrations, auditing, and automated tests.

## Domain model

```text
Route
  ↓
ScheduledTrip
  ↓
Assignment
  ├── Bus
  ├── Driver
  └── Conductor
```

Users authenticate to the API and are authorized with one of these roles:

- `ROLE_ADMIN`
- `ROLE_OPERATIONS_MANAGER`
- `ROLE_USER`

## Architecture

The code is organized by feature while retaining normal application layers inside each feature.

```text
com.tritonptms.ptms/
├── PtmsApplication.java
├── feature/
│   ├── assignment/
│   ├── auth/
│   ├── bus/
│   │   └── audit/
│   ├── dashboard/
│   ├── employee/
│   ├── reference/
│   ├── route/
│   ├── schedule/
│   └── user/
├── infrastructure/
│   ├── audit/
│   ├── config/
│   ├── security/
│   └── seed/
└── common/
    ├── exception/
    ├── persistence/
    └── web/
```

The root packages have intentionally different responsibilities:

- `feature`: user-facing and business capabilities, organized package-by-feature;
- `infrastructure`: Spring/Hibernate/security/environment mechanisms that support the application;
- `common`: small reusable foundations shared by multiple features.

Feature packages still retain normal Controller → Service → Repository layering internally. Bus revision endpoints live with the Bus feature, while the Envers revision machinery lives under `infrastructure.audit`.

Typical request flow:

```text
HTTP
  ↓
Spring Security
  ↓
Controller
  ↓
Request DTO + Jakarta Validation
  ↓
Service + transaction boundary
  ↓
Repository
  ↓
Spring Data JPA / Hibernate
  ↓
PostgreSQL / PostGIS
```

Cross-cutting responsibilities:

- schema/versioning: Flyway;
- authentication: Spring Security session + CSRF;
- errors: `ProblemDetail` + `@RestControllerAdvice`;
- metadata auditing: Spring Data JPA auditing;
- revision history: Hibernate Envers/Spring Data Envers for `Bus`;
- health: Spring Boot Actuator.

See [Architecture](docs/ARCHITECTURE.md) for more detail.

## Main technologies

- Java 21
- Spring Boot 3.5.4
- Spring MVC
- Spring Security
- Jakarta Validation
- Spring Data JPA / Hibernate ORM
- Hibernate Spatial + JTS
- PostgreSQL 16 / PostGIS
- Flyway
- Spring Data JPA Auditing
- Hibernate Envers + Spring Data Envers
- Spring Boot Actuator
- Maven Wrapper
- JUnit 5, Mockito, Spring Security Test
- Testcontainers with PostGIS

## Prerequisites

For local development:

1. JDK 21
2. Docker Desktop (or another Docker-compatible engine)
3. Git

You do **not** need to install Maven separately. Use `mvnw.cmd` on Windows or `./mvnw` on Linux/macOS.

Verify Java and Maven on Windows:

```powershell
java -version
.\mvnw.cmd -version
```

## Run locally on Windows

### 1. Start PostgreSQL/PostGIS

```powershell
docker compose -f compose.dev.yml up -d
```

The development database defaults are:

```text
database: bus_transport_db
username: postgres
password: root
port:     5432
```

These credentials are **development-only**.

### 2. Start the application

```powershell
.\mvnw.cmd "-Dspring-boot.run.profiles=dev" spring-boot:run
```

Flyway runs automatically. Hibernate uses `ddl-auto=validate`, so Flyway owns schema changes.

The default development seeder is enabled only for the `dev` profile. It creates sample data and this development administrator when missing:

```text
username: devadmin
password: Admin123!
```

This credential does not exist in the `prod` profile.

### 3. Health check

```text
GET http://localhost:8080/actuator/health
```

Expected result:

```json
{"status":"UP"}
```

### Optional `.env.dev` workflow

Copy the example file, edit it if needed, then use the helper script:

```powershell
Copy-Item .env.example .env.dev
.\scripts\run-dev.ps1
```

`.env.dev` is ignored by Git.

## Tests

The persistence/integration tests use Testcontainers with a real PostGIS image. Docker must be running.

```powershell
.\mvnw.cmd clean test
```

Build the final JAR:

```powershell
.\mvnw.cmd clean package
```

Or run both checks through:

```powershell
.\scripts\verify.ps1
```

Integration coverage includes Flyway startup, Hibernate validation, PostGIS geometry persistence, login/session behavior, CSRF, role authorization, standardized errors, Bus CRUD/revisions, and Actuator health.

## Reset the development database

This deletes the local Docker development volume:

```powershell
docker compose -f compose.dev.yml down -v
docker compose -f compose.dev.yml up -d
```

Restart the application and Flyway will rebuild the schema from zero.

## Session authentication and CSRF

PTMS intentionally uses browser-style session authentication rather than JWT.

For Postman or another manual API client:

1. `GET /api/auth/csrf` and retain the cookies.
2. Read the returned token and send it as `X-XSRF-TOKEN`.
3. `POST /api/auth/login` with JSON credentials.
4. Retain the returned `JSESSIONID` cookie.
5. Request `GET /api/auth/csrf` again after login because the CSRF token is rotated.
6. For `POST`, `PUT`, `PATCH`, and `DELETE`, send the new `X-XSRF-TOKEN` header and session cookies.
7. Use `GET /api/auth/me` to inspect the authenticated user.
8. `POST /api/auth/logout` with the CSRF token to end the session.

Example login body:

```json
{
  "username": "devadmin",
  "password": "Admin123!"
}
```

See [API and authentication guide](docs/API.md) for endpoint groups and roles.

## Configuration and profiles

A runtime profile must be selected explicitly. There is no silent fallback to `dev`.

Profiles:

- `dev` – local PostGIS defaults, development logs, optional sample seeding;
- `test` – datasource supplied by Testcontainers;
- `prod` – requires externally supplied database/CORS configuration and disables sample seeding.

Important production variables:

```text
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://<host>:5432/<database>
DB_USERNAME=<username>
DB_PASSWORD=<secret>
CORS_ALLOWED_ORIGINS=https://<frontend-origin>
COOKIE_SECURE=true
```

No production database password or production user password is committed to the repository.

## Auditing

Two audit concerns are intentionally separated:

1. Spring Data JPA auditing records `createdAt`, `updatedAt`, `createdBy`, and `updatedBy` on core entities.
2. `Bus` additionally retains full revisions through Envers.

Admin-only history endpoint:

```text
GET /api/buses/{id}/revisions
```

The former duplicate `ActionLog`/reflection-based audit framework was removed.

## Actuator

`/actuator/health` is public so container/platform health checks can use it. Other actuator endpoints follow normal authentication rules. The production profile exposes only `health` and `info`.

## CI

`.github/workflows/ci.yml` uses Java 21 and runs:

```text
./mvnw clean test
./mvnw -DskipTests package
```

The integration tests rely on Testcontainers, and GitHub-hosted Ubuntu runners provide Docker for them.

## Additional documentation

- [Architecture](docs/ARCHITECTURE.md)
- [API and authentication](docs/API.md)
- [Final modernization handoff](PHASE_3_HANDOFF.md)
