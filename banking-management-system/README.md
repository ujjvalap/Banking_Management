# Banking Management System

This project currently provides customer registration and authentication, a Thymeleaf sign-in/profile page, server-managed browser sessions, and a stateless JWT API. Account management, transfers, statements, email delivery/verification, password reset, MFA, and admin management endpoints are not implemented.

## Requirements and local setup

- Java 17+
- MySQL 8+
- The included Maven wrapper

Create a MySQL database or use the default JDBC URL, which creates `banking_db` when the MySQL account has permission. Set `DB_USERNAME` and `DB_PASSWORD` for your database. `DB_URL` overrides the default.

Set `JWT_SECRET` to a Base64-encoded secret containing at least 32 random bytes (for example, generate it with `openssl rand -base64 32`). A valid secret is required at startup. The test profile uses an isolated H2 database and a test-only key.

From the Maven project directory (`banking-management-system`), run:

```powershell
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080). The default `JPA_DDL_AUTO=update` is intended for local/demo use; set it to `validate` and manage schema changes with migrations in production. Set `COOKIE_SECURE=true` when serving the browser UI over HTTPS.

## Authentication API

The versioned API uses `/api/v1`; `/api/auth` is retained as an alias for the authentication routes. Error responses include `timestamp`, `status`, `error`, `message`, `path`, and `validationErrors`.

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/v1/register` | Public | Create a customer; return access and refresh tokens |
| POST | `/api/v1/login` | Public | Authenticate and return access and refresh tokens |
| GET | `/api/v1/me` | Bearer token | Return the authenticated user's safe profile |
| POST | `/api/v1/refresh` | Public, refresh token required | Rotate the refresh token and issue a new access token |
| POST | `/api/v1/logout` | Bearer token | Revoke all refresh tokens for the authenticated user |

The `/api/auth/register`, `/api/auth/login`, `/api/auth/me`, `/api/auth/refresh`, and `/api/auth/logout` aliases behave the same way.

Example registration request:

```json
{
  "fullName": "Alex Morgan",
  "email": "alex@example.com",
  "password": "StrongPass123!",
  "phone": "+1 555 123 4567",
  "address": "12 Market Street",
  "pan": "ABCDE1234F"
}
```

Passwords must be at least 12 characters with uppercase, lowercase, a number, and a special character, and cannot exceed 72 UTF-8 bytes. Public registration always assigns the `CUSTOMER` role. API token responses contain `accessToken`, `refreshToken`, `tokenType`, and `expiresIn` (seconds). Send the access token as `Authorization: Bearer <accessToken>`. Refresh tokens are rotated on use; reusing a revoked token invalidates that token family.

`/api/admin/**` requires the `ADMIN` role and `/api/customer/**` requires `CUSTOMER`; no admin or banking resource endpoints are currently provided.

## Security and tests

- Passwords are stored as BCrypt hashes (work factor 12).
- The JSON API is stateless and uses signed, short-lived JWT access tokens; refresh tokens are random, stored as SHA-256 hashes, rotated, and revocable.
- The browser UI uses Spring Security form login, CSRF protection, session ID rotation, and HttpOnly/SameSite cookies.
- Login attempts are temporarily locked after the configured number of failures.
- CORS origins can be configured with `CORS_ALLOWED_ORIGINS`; local development defaults allow ports 3000 and 5173.
- Set database credentials and signing keys through environment variables or a secrets manager; do not commit secrets.

Run the automated API and browser tests with:

```powershell
.\mvnw.cmd test
```
