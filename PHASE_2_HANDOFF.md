# PTMS Modernization - Phase 2 Handoff

## Status

Phase 2 implements the application/API modernization described in the Phase 2 plan. It builds on the Phase 1 foundation and intentionally does **not** finish auditing, tests, final integration verification, or final documentation. Those remain Phase 3 work.

## What changed

### API contracts and mapping

- Public controllers no longer use JPA entities as request/response bodies.
- Feature DTOs are split into request/response contracts where useful.
- Manual mapper classes now handle entity <-> API conversion.
- The old `BaseResponse<T>` success envelope was removed; success responses return DTOs, lists, or `Page<DTO>` directly.
- Create endpoints normally return `201 Created`; deletes return `204 No Content`.
- Relationship inputs use IDs instead of nested persistence-shaped objects. For example, scheduled-trip requests use `routeId`.

Modernized feature contracts include:

- buses
- routes
- drivers
- conductors
- employee search
- scheduled trips
- assignments
- users
- action-log response exposure
- authentication/current-user response

### Validation

- Request bodies use `@Valid` consistently.
- DTO fields use Jakarta Bean Validation (`@NotBlank`, `@NotNull`, `@Size`, `@Positive`, `@Email`, `@Pattern`, date constraints, etc.).
- Cross-field/business checks that do not fit annotations remain in services, for example trip start/end time ordering.

### Controllers and services

- Controllers are thin HTTP adapters and no longer query repositories directly.
- Services remain transaction boundaries.
- Query operations use the Phase 1 read-only transaction foundation.
- Write operations use `@Transactional`.
- Use-case authorization is applied with `@PreAuthorize` where it provides a useful second boundary.

### Session security

Authentication remains **Spring Security session authentication**, not JWT.

The login flow is now:

```text
POST /api/auth/login (JSON + CSRF token)
    -> JsonUsernamePasswordAuthenticationFilter
    -> AuthenticationManager
    -> DaoAuthenticationProvider
    -> UserDetailsServiceImpl
    -> BCrypt PasswordEncoder
    -> AuthenticatedUser principal
    -> SecurityContext
    -> HttpSession / JSESSIONID
```

Changes include:

- removed the parallel `formLogin()` mechanism;
- removed login logic from `AuthController`;
- the custom JSON filter is the only login mechanism;
- the filter explicitly saves the authenticated `SecurityContext` to an HTTP-session-backed repository;
- session fixation protection changes the session ID after login;
- authentication success/failure handlers only write API responses;
- the JPA `User` entity no longer implements `UserDetails`;
- `AuthenticatedUser` is the Spring Security principal;
- controllers use `@AuthenticationPrincipal` rather than reading `SecurityContextHolder` directly;
- Spring Security logout handles session invalidation at `POST /api/auth/logout`.

### Authorization

- `@EnableMethodSecurity` is enabled.
- Broad endpoint rules remain in `SecurityFilterChain`.
- Use-case write/admin restrictions use `@PreAuthorize` in services.
- Manual role loops were removed from the modernized application layer.

Current broad access model remains close to the existing project behavior:

- buses/routes: ADMIN, OPERATIONS_MANAGER, USER can read; service method security restricts writes to ADMIN/OPERATIONS_MANAGER;
- assignments/scheduled trips/drivers/conductors/employees/dashboard/action logs: ADMIN or OPERATIONS_MANAGER;
- users/audit/admin areas: ADMIN.

### CSRF

CSRF is **enabled** for normal development and production.

The application uses `CookieCsrfTokenRepository`:

- cookie: `XSRF-TOKEN`
- request header: `X-XSRF-TOKEN`
- endpoint for obtaining/refreshing a token: `GET /api/auth/csrf`

The CSRF token is rotated/cleared after successful authentication. Therefore obtain a fresh token after login before the next state-changing request.

### CORS

CORS is centralized in `SecurityConfig` and obtains allowed origins from Phase 1 `AppProperties` configuration.

Development defaults remain:

```text
http://localhost:5173
http://localhost:3000
```

Credentials are enabled so the frontend can use the session cookie.

### Error handling

MVC/API errors now use one `@RestControllerAdvice` based on Spring `ProblemDetail`.

Covered categories include:

- 400 malformed request / invalid parameters / validation
- 401 unauthenticated
- 403 forbidden
- 404 resource not found
- 409 conflicts/data integrity conflicts
- 415 unsupported media type
- 500 unexpected server errors

Validation errors add an `errors` property keyed by field/path.

Security failures that occur before MVC use JSON `AuthenticationEntryPoint`, `AccessDeniedHandler`, and login failure handlers with the same ProblemDetail style instead of HTML responses.

### User/password creation

- New users are created with a cryptographically generated temporary password instead of one shared hard-coded password.
- The password is BCrypt encoded before persistence.
- `CreateUserResponse` returns the generated temporary password once so the current learning project remains usable without building email/password-reset infrastructure in Phase 2.
- Development-only seed credentials remain in `DevDataSeeder`; they are still protected by the `dev` profile and are not production seed data.

## API changes worth noticing

The modernization intentionally removes some legacy routes/contracts rather than keeping duplicate compatibility APIs:

```text
POST /api/users
    replaces the old /api/users/register style operation

GET /api/scheduled-trips/search
    replaces /api/scheduled-trips/search-trips

GET /api/action-logs
    replaces /api/action-logs/all
```

Examples of new relationship-oriented request shapes:

```json
{
  "routeId": 1,
  "direction": "TO",
  "expectedStartTime": "08:00:00",
  "expectedEndTime": "10:00:00"
}
```

```json
{
  "scheduledTripId": 1,
  "busId": 1,
  "driverId": 1,
  "conductorId": 1,
  "date": "2026-09-13",
  "actualStartTime": null,
  "actualEndTime": null,
  "status": "SCHEDULED"
}
```

## Fresh local verification on Windows / PowerShell

Java 21 is required. Maven itself does not need to be installed separately because the project includes the Maven wrapper.

### 1. Compile production code

```powershell
.\mvnw.cmd clean compile
```

### 2. Package while Phase 3 tests are still deferred

PowerShell should quote Maven `-D` arguments:

```powershell
.\mvnw.cmd "-Dmaven.test.skip=true" clean package
```

### 3. Start the development PostgreSQL/PostGIS database

```powershell
docker compose -f compose.dev.yml up -d
```

### 4. Run the application with the explicit dev profile

```powershell
.\mvnw.cmd "-Dmaven.test.skip=true" "-Dspring-boot.run.profiles=dev" spring-boot:run
```

Development seed login remains:

```text
username: devadmin
password: Admin123!
```

This credential exists only in the development seeder.

## Manual session + CSRF test sequence

For Postman or another manual HTTP client, keep its cookie jar enabled.

### 1. Obtain a pre-login CSRF token

```http
GET /api/auth/csrf
```

The response contains the token and the server also issues an `XSRF-TOKEN` cookie.

### 2. Login

```http
POST /api/auth/login
Content-Type: application/json
X-XSRF-TOKEN: <token from step 1>
```

```json
{
  "username": "devadmin",
  "password": "Admin123!"
}
```

Keep the cookies returned by the server, especially `JSESSIONID`.

### 3. Obtain a new CSRF token after login

```http
GET /api/auth/csrf
```

Use the new `XSRF-TOKEN`/token value for subsequent POST, PUT, PATCH, DELETE, and logout requests.

### 4. Confirm the session principal

```http
GET /api/auth/me
```

### 5. Logout

```http
POST /api/auth/logout
X-XSRF-TOKEN: <current token>
```

Expected status: `204 No Content`.

## Verification already performed in the implementation environment

Static/source verification passed:

- 104 production Java files parsed using the Java 21 compiler parser with zero syntax errors;
- internal package/path/import scan found zero unresolved project imports;
- no production trailing-whitespace issues;
- `pom.xml` parses as XML;
- Unix `mvnw` passes shell syntax validation;
- no active `BaseResponse` usage;
- no active `formLogin()` configuration;
- no global CSRF disable configuration;
- no repository injection found in controllers;
- modernized controllers use explicit request DTOs with `@Valid`;
- no main resource controller directly exposes a JPA entity;
- `SecurityContextHolder` usage is confined to the old audit subsystem that Phase 3 will simplify.

A full Maven compilation could not be executed in the implementation sandbox because that environment cannot resolve Maven Central and does not have a populated Maven dependency cache. **Run `./mvnw.cmd clean compile` locally as the first authoritative Phase 2 build check.**

## Intentionally deferred to Phase 3

Do not treat the remaining items as Phase 2 regressions merely because they still exist:

- old/incomplete test sources and test architecture;
- full Spring Security integration tests;
- CSRF/session integration tests;
- PostgreSQL/PostGIS integration verification;
- audit architecture cleanup;
- Envers simplification/re-enablement decision;
- removal/replacement of duplicate/legacy action-log infrastructure;
- deprecated/unchecked code inside the existing `AuditService`;
- final README/API documentation;
- final CI/build verification and dead-code cleanup.

The next phase should use this Phase 2 project as its input rather than restoring old API/security patterns for test compatibility.
