# PTMS Architecture

## Style

PTMS is a **feature-oriented layered monolith**. The root package separates application capabilities from technical infrastructure and shared foundations, while package-by-feature keeps each business capability together.

```text
com.tritonptms.ptms/
├── feature/          # what PTMS does
├── infrastructure/   # technical mechanisms used by PTMS
├── common/           # small shared foundations
└── PtmsApplication.java
```

A typical feature contains:

```text
feature/bus/
├── Bus.java
├── BusController.java
├── BusService.java
├── BusRepository.java
├── BusSpecification.java
├── BusMapper.java
├── audit/
└── dto/
    ├── BusRequest.java
    └── BusResponse.java
```

`feature.bus.audit` exposes Bus-specific revision use cases. Generic Envers/JPA auditing machinery is kept separately in `infrastructure.audit`.

## Responsibilities

### Controller

Controllers bind HTTP input, trigger Jakarta validation, call services, select status codes, and return response DTOs. They do not access repositories directly.

### Service

Services are the application/business orchestration boundary. Write operations use `@Transactional`; read-heavy services use read-only transactions where useful. Method-level authorization is applied here when a use case has role requirements.

### Repository

Repositories use Spring Data JPA, derived queries, pagination, and Specifications. Database-specific persistence is left to Hibernate/PostgreSQL instead of controllers or mappers.

### DTO and mapper

JPA entities are not public API contracts. Request and response DTOs are explicitly mapped with small manual mapper classes.

## Persistence

Flyway is the only schema migration mechanism. Hibernate runs with:

```text
spring.jpa.hibernate.ddl-auto=validate
```

The schema uses PostgreSQL/PostGIS. `Route.routePath` is a JTS `LineString` persisted as `geography(LineString,4326)`.

## Authentication and authorization

Authentication is stateful Spring Security session authentication:

```text
JSON login
  ↓
JsonUsernamePasswordAuthenticationFilter
  ↓
AuthenticationManager
  ↓
UserDetailsService + BCrypt
  ↓
SecurityContext
  ↓
HttpSession / JSESSIONID
```

CSRF is enabled because authentication is cookie/session based. URL rules provide coarse protection and `@PreAuthorize` protects application use cases.

The JPA `User` entity and the Spring Security `AuthenticatedUser` principal are separate types.

## Error handling

MVC exceptions flow to a single `@RestControllerAdvice` and are represented with Spring `ProblemDetail`. Security 401/403 errors are written in the same `application/problem+json` style by the security entry point/access-denied handler.

## Auditing

### Metadata

`AuditableEntity` uses Spring Data JPA auditing for:

```text
createdAt
updatedAt
createdBy
updatedBy
```

`SecurityAuditorAware` uses the authenticated username or `system` for non-user work such as development seeding.

### Revision history

Only `Bus` keeps full historical revisions. `BusRepository` also implements Spring Data Envers `RevisionRepository`, and the admin-only audit endpoint maps revision internals to `BusRevisionResponse`.

This intentionally replaces the previous duplicate global `ActionLog` and reflection-based diff infrastructure.

## Tests

Fast tests cover mapping/service behavior. A PostGIS Testcontainer is used for the Spring integration suite so database tests exercise the same important PostgreSQL/PostGIS behavior used by the application rather than H2 approximations.
