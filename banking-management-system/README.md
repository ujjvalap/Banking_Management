# Banking Management System — Authentication

This project currently implements the **authentication module only**. It includes customer registration, login, a Thymeleaf sign-in/profile UI, server-managed browser sessions, and a JWT-protected API. Bank accounts, transfers, statements, password resets, MFA, and admin management workflows are not implemented.

## Requirements

- Java 17+
- Maven (or the included Maven wrapper)
- MySQL 8+

## Run locally

1. Create a MySQL database, or allow the configured local JDBC URL to create `banking_management_system`.
2. Set `DB_USERNAME` and `DB_PASSWORD` for your local MySQL instance. `DB_URL` can override the default JDBC URL. Database credentials have no source-code defaults.
3. Set `JWT_SECRET` to a Base64-encoded secret with at least 32 random bytes. Generate one with `openssl rand -base64 32`. The application refuses to start without a valid secret; the test profile uses a test-only key.
4. For local HTTPS, set `SESSION_COOKIE_SECURE=true` (leave it false only for local HTTP development).
5. Start the app:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

6. Open [http://localhost:8080](http://localhost:8080).

Provide database credentials, admin provisioning values, and signing keys through environment variables or a secrets manager, never source control. Hibernate schema updates are enabled for this demo; use managed migrations and a validated schema in production.

## Authentication API

All JSON responses use standard HTTP status codes. Errors have the form:

```json
{
  "timestamp": "2026-01-01T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed.",
  "path": "/api/auth/register",
  "validationErrors": {
    "email": "must be a well-formed email address"
  }
}
```

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Create a customer and profile; returns a JWT |
| POST | `/api/auth/login` | Public | Verify credentials and return a JWT |
| GET | `/api/auth/me` | Bearer token | Return the signed-in user's safe profile |

Example registration body:

```json
{
  "fullName": "Alex Morgan",
  "email": "alex@example.com",
  "password": "StrongPass123",
  "phone": "+1 555 123 4567",
  "address": "12 Market Street",
  "pan": "ABCDE1234F"
}
```

Send the returned token on protected API calls:

```text
Authorization: Bearer <accessToken>
```

## Architecture and security

- `controller → service → repository`: controllers handle HTTP, the service owns registration/login behavior, and Spring Data repositories handle persistence.
- `AppUser` and `CustomerProfile` are separate JPA entities with a one-to-one relationship. Public registration always assigns `CUSTOMER`; clients cannot grant themselves the `ADMIN` role.
- Passwords are BCrypt-hashed (work factor 12). New passwords must be at least 12 characters with uppercase, lowercase, and numeric characters. Passwords are limited to 72 UTF-8 bytes for BCrypt compatibility.
- The Thymeleaf browser UI uses Spring Security form login, server-managed sessions, CSRF protection, session ID rotation, HttpOnly/SameSite cookies, and a 30-minute idle timeout. Set `SESSION_COOKIE_SECURE=true` behind HTTPS.
- The JSON API remains stateless: JWTs are HMAC-signed, expire after 15 minutes, and user roles are reloaded from the database for each authenticated request. Browser sessions do not expose or store API tokens.
- The web security policy includes a restrictive Content Security Policy, frame protection, and a strict referrer policy. API CORS origins are configurable with `CORS_ALLOWED_ORIGINS`.
- `/api/admin/**` requires `ADMIN`; `/api/customer/**` requires `CUSTOMER`; other `/api/**` routes require a valid JWT.
- Validation errors, duplicate data, invalid credentials, forbidden requests, and unexpected server errors return consistent JSON error bodies.

The Thymeleaf page and static assets are served by Spring from `src/main/resources/templates` and `src/main/resources/static`. Configure `CORS_ALLOWED_ORIGINS` if a separate frontend needs API access. The default allows local development origins on ports 3000 and 5173.

To provision the first administrator, set `ADMIN_EMAIL`, `ADMIN_PASSWORD`, `ADMIN_FULL_NAME`, `ADMIN_PHONE`, `ADMIN_ADDRESS`, and `ADMIN_PAN` before the first start. The admin is created only when all six values validate; public registration never accepts a role. Admin passwords must meet the same password requirements. Do not put admin credentials in source control.

## Tests

```powershell
.\mvnw.cmd test
```

Tests use an isolated in-memory H2 database; the application itself uses MySQL.
