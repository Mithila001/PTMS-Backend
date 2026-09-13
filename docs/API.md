# PTMS API and Authentication Guide

Base URL for local development:

```text
http://localhost:8080
```

## Authentication flow

PTMS uses Spring Security sessions and CSRF protection.

### 1. Obtain a CSRF token

```http
GET /api/auth/csrf
```

Retain the cookies and the returned token. Send the token in:

```text
X-XSRF-TOKEN: <token>
```

### 2. Login

```http
POST /api/auth/login
Content-Type: application/json
X-XSRF-TOKEN: <token>
```

```json
{
  "username": "devadmin",
  "password": "Admin123!"
}
```

A successful response establishes a `JSESSIONID` session cookie.

### 3. Refresh CSRF after login

Authentication rotates/clears the previous token. Call:

```http
GET /api/auth/csrf
```

again and use the new token for later state-changing requests.

### 4. Current user

```http
GET /api/auth/me
```

### 5. Logout

```http
POST /api/auth/logout
X-XSRF-TOKEN: <current-token>
```

Expected status: `204 No Content`.

## Roles

```text
ADMIN
OPERATIONS_MANAGER
USER
```

Spring authorities are stored internally as `ROLE_ADMIN`, `ROLE_OPERATIONS_MANAGER`, and `ROLE_USER`.

## Endpoint groups

| Endpoint group | Read | Write | Notes |
|---|---|---|---|
| `/api/buses/**` | authenticated ADMIN / OPERATIONS_MANAGER / USER | ADMIN / OPERATIONS_MANAGER | Supports search/pagination endpoints |
| `/api/routes/**` | authenticated ADMIN / OPERATIONS_MANAGER / USER | ADMIN / OPERATIONS_MANAGER | Route geometry is not exposed as a JPA entity |
| `/api/scheduled-trips/**` | ADMIN / OPERATIONS_MANAGER | ADMIN / OPERATIONS_MANAGER | CRUD/search |
| `/api/assignments/**` | ADMIN / OPERATIONS_MANAGER | ADMIN / OPERATIONS_MANAGER | CRUD/search |
| `/api/drivers/**` | ADMIN / OPERATIONS_MANAGER | ADMIN / OPERATIONS_MANAGER | CRUD |
| `/api/conductors/**` | ADMIN / OPERATIONS_MANAGER | ADMIN / OPERATIONS_MANAGER | CRUD |
| `/api/employees/**` | ADMIN / OPERATIONS_MANAGER | n/a | Employee search endpoints |
| `/api/users/**` | ADMIN | ADMIN | User administration |
| `/api/dashboard/**` | ADMIN / OPERATIONS_MANAGER | n/a | Dashboard metrics |
| `/api/buses/{id}/revisions` | ADMIN | n/a | Bus Envers history |
| `/api/enums/**` | public | n/a | Reference enum values |
| `/api/auth/csrf` | public | n/a | CSRF bootstrap |
| `/api/auth/login` | public + CSRF | n/a | JSON login filter |
| `/api/auth/me` | authenticated | n/a | Current principal |
| `/api/auth/logout` | authenticated + CSRF | n/a | Session logout |
| `/actuator/health` | public | n/a | Health check |

## Request/response style

Public APIs use request/response DTOs rather than JPA entities. Pagination is rendered through Spring Data's stable page DTO mode: results are under `content` and page metadata is under `page`.

State-changing requests use normal status codes such as:

```text
200 OK
201 Created
204 No Content
```

## Errors

Errors use `application/problem+json` based on Spring `ProblemDetail`.

Representative statuses:

```text
400 malformed input / validation
401 authentication required or invalid credentials
403 authenticated but forbidden / invalid CSRF
404 resource not found
409 data/state conflict
500 unexpected server error
```

Validation responses include an `errors` property keyed by field name.

## Bus revision history

Admin-only:

```http
GET /api/buses/{busId}/revisions
```

Each response item contains the revision number, timestamp, actor, revision type, and a `BusResponse` snapshot.
