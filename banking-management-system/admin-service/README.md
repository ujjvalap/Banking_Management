# Banking Management System -- Admin Service

## 1. Project Overview

The **Admin Service** is a backend microservice within the Banking
Management System. It provides administrative functionality for
monitoring customers, bank accounts, and transactions through a web
dashboard and REST APIs.

The service communicates with the Auth, Account, and Transaction
microservices to retrieve banking information and perform administrative
operations. It also maintains administrative audit information in a
MySQL database.

## 2. Objectives

-   Provide a centralized dashboard for banking administrators.
-   Retrieve customer and account information from other microservices.
-   Allow authorized administrators to update account status.
-   Retrieve and filter transaction records.
-   Secure administrative endpoints using JWT authentication and
    role-based authorization.
-   Record administrative activities for auditing.
-   Handle application and remote-service errors consistently.

## 3. Features

### Admin Dashboard

-   Web-based administrative interface.
-   Rendered using Thymeleaf.
-   Accessible at the application's root URL.

### Customer Management

-   Retrieves customer information through `AuthClient`.
-   Uses `CustomerDto` to transfer customer data.

### Account Management

-   Retrieves account information through `AccountClient`.
-   Supports account-status updates.
-   Uses `AccountDto` and `StatusUpdateRequest`.

### Transaction Monitoring

-   Retrieves transaction information through `TransactionClient`.
-   Supports filtering by date and transaction type.
-   Uses `TransactionDto` and `TransactionFilterRequest`.

### Security

-   JWT bearer-token authentication.
-   Token validation and claim extraction.
-   Role-based authorization for administrative endpoints.
-   Stateless request security using Spring Security.

### Audit Logging

-   Uses `AdminAuditLog` to represent audit records.
-   Uses `AdminAuditLogRepository` for persistence.

### Exception Handling

-   Centralized handling through `GlobalExceptionHandler`.
-   Handles invalid arguments and remote-service errors.
-   Uses `ErrorResponse` for structured error details.

## 4. Technology Stack

  Technology          Purpose
  ------------------- ----------------------------------
  Java 17             Programming language
  Spring Boot 4.2.0   Application framework
  Spring Web          REST API development
  Spring Security     Authentication and authorization
  JWT                 Token-based security
  Spring Data JPA     Database access
  Hibernate           Object-relational mapping
  MySQL               Relational database
  Thymeleaf           Server-side dashboard rendering
  RestClient          Inter-service HTTP communication
  Maven               Build and dependency management
  HTML                Dashboard structure

## 5. Architecture

The Admin Service follows a microservice architecture. It handles
administrative requests and communicates with the other services through
dedicated REST clients.

``` text
                       Admin User
                           |
                           v
                    Admin Dashboard
                           |
                           v
                     Admin Service
                      Port: 8084
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
         AuthClient   AccountClient  TransactionClient
             |             |             |
             v             v             v
        Auth Service  Account Service  Transaction Service

                     Admin Service
                           |
                 +---------+---------+
                 |                   |
                 v                   v
             MySQL Database      Audit Logging
           admin_service_db
```

### Component Responsibilities

-   **Admin Service:** Handles administrative business logic and exposes
    APIs.
-   **Auth Service:** Supplies customer information through the Auth
    Service integration.
-   **Account Service:** Supplies account information and processes
    account-status updates.
-   **Transaction Service:** Supplies transaction records.
-   **MySQL:** Stores administrative audit information.
-   **JWT Security:** Validates access tokens and enforces administrator
    authorization.

## 6. Project Structure

``` text
admin-service/
├── .mvn/
│   └── wrapper/
├── src/
│   ├── main/
│   │   ├── java/com/sprint/bank/admin/
│   │   │   ├── AdminServiceApplication.java
│   │   │   ├── client/
│   │   │   │   ├── AccountClient.java
│   │   │   │   ├── AuthClient.java
│   │   │   │   └── TransactionClient.java
│   │   │   ├── config/
│   │   │   │   └── RestClientConfig.java
│   │   │   ├── controller/
│   │   │   │   └── AdminController.java
│   │   │   ├── dto/
│   │   │   │   ├── AccountDto.java
│   │   │   │   ├── CustomerDto.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── StatusUpdateRequest.java
│   │   │   │   ├── TransactionDto.java
│   │   │   │   └── TransactionFilterRequest.java
│   │   │   ├── entity/
│   │   │   │   └── AdminAuditLog.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── RemoteServiceException.java
│   │   │   ├── repository/
│   │   │   │   └── AdminAuditLogRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtFilter.java
│   │   │   │   ├── JwtUtil.java
│   │   │   │   └── SecurityConfig.java
│   │   │   └── service/
│   │   │       ├── AdminService.java
│   │   │       └── impl/
│   │   │           └── AdminServiceImpl.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   │           └── admin-dashboard.html
│   └── test/
│       └── java/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
```

## 7. Package Responsibilities

  -----------------------------------------------------------------------
  Package                             Responsibility
  ----------------------------------- -----------------------------------
  `client`                            Communicates with Auth, Account,
                                      and Transaction services

  `config`                            Configures shared components such
                                      as the REST client

  `controller`                        Handles incoming HTTP requests

  `dto`                               Defines request and response
                                      data-transfer objects

  `entity`                            Defines database entities

  `exception`                         Contains custom exceptions and
                                      centralized error handling

  `repository`                        Provides database persistence
                                      operations

  `security`                          Implements JWT processing and
                                      authorization configuration

  `service`                           Defines administrative business
                                      operations

  `service.impl`                      Implements the business logic
  -----------------------------------------------------------------------

## 8. REST API Documentation

**Base URL:** `http://localhost:8084`

The following administrative endpoints are implemented.

### 8.1 Get All Customers

``` http
GET /api/admin/customers
```

Retrieves customer information through the Auth Service integration.

**Authorization:** ADMIN role required.

### 8.2 Get All Accounts

``` http
GET /api/admin/accounts
```

Retrieves account information through the Account Service integration.

**Authorization:** ADMIN role required.

### 8.3 Update Account Status

``` http
PUT /api/admin/accounts/{id}/status
```

Updates the status of the account identified by the `id` path parameter
through the Account Service.

**Request DTO:** `StatusUpdateRequest`

**Authorization:** ADMIN role required.

### 8.4 Get Transactions

``` http
GET /api/admin/transactions
```

Retrieves transaction information through the Transaction Service. The
endpoint supports filtering by date and transaction type.

**Request DTO:** `TransactionFilterRequest`

**Authorization:** ADMIN role required.

> Refer to `AdminController.java` for the exact query parameter names,
> request fields, and response structures. Those details should match
> the implementation.

## 9. JWT Authentication and Authorization

The service uses Spring Security with JWT-based authentication.

### Authentication Flow

1.  A user authenticates through the Auth Service.
2.  The Auth Service issues an access token.
3.  The client sends the token with requests to protected Admin Service
    endpoints.
4.  `JwtFilter` intercepts the request and extracts the bearer token.
5.  `JwtUtil` validates the token and reads the required claims.
6.  The authenticated identity and role are made available to Spring
    Security.
7.  The security configuration permits or rejects the request according
    to the user's role.

**Authorization header:**

``` http
Authorization: Bearer <ACCESS_TOKEN>
```

Administrative APIs require a valid token with the ADMIN role. A
CUSTOMER token is not sufficient.

The JWT signing secret and token claims must be compatible with the Auth
Service. Do not commit real tokens or signing secrets to source control.

## 10. Database Configuration

The Admin Service uses MySQL.

**Database name:**

``` sql
CREATE DATABASE admin_service_db;
```

The application configuration is located at:

``` text
src/main/resources/application.properties
```

Example of the sensitive configuration entries:

``` properties
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

Set the database URL, username, JPA settings, service URLs, and other
properties according to the existing `application.properties` file.

The application expects the database password and JWT secret to be
supplied through environment variables. Never replace these placeholders
with real credentials in a committed file.

## 11. Prerequisites

Install and configure:

-   JDK 17 or the Java version required by the project build.
-   MySQL Server.
-   Git.
-   An IDE such as IntelliJ IDEA, Eclipse, or VS Code.
-   Maven, or use the Maven Wrapper included in the project.

Check Java:

``` bash
java -version
```

Check Maven:

``` bash
mvn -version
```

## 12. Installation and Setup

### Step 1: Clone the repository

``` bash
git clone https://github.com/ujjvalap/Banking_Management.git
```

### Step 2: Navigate to the Admin Service

``` bash
cd Banking_Management/banking-management-system/admin-service
```

### Step 3: Create the database

Execute the following statement in MySQL:

``` sql
CREATE DATABASE admin_service_db;
```

### Step 4: Configure environment variables

In Windows PowerShell, set the variables for the current terminal
session:

``` powershell
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="your_shared_jwt_secret"
```

Replace the example values with your own local configuration. The JWT
secret must match the secret expected by the Auth Service.

### Step 5: Build the application

Using Maven:

``` bash
mvn clean install
```

Or, on Windows, using the Maven Wrapper:

``` powershell
.\mvnw.cmd clean install
```

### Step 6: Run the application

Using Maven:

``` bash
mvn spring-boot:run
```

Or, on Windows:

``` powershell
.\mvnw.cmd spring-boot:run
```

### Step 7: Open the dashboard

Visit:

``` text
http://localhost:8084/
```

The dashboard should load when the application has started successfully.

## 13. Testing

Run the available tests:

``` bash
mvn test
```

On Windows, the Maven Wrapper can be used:

``` powershell
.\mvnw.cmd test
```

Clean and compile:

``` bash
mvn clean compile
```

Package the application:

``` bash
mvn clean package
```

The project contains a test source tree under `src/test/java`. Add and
maintain unit and integration tests as the service evolves.

## 14. Inter-Service Communication

The service uses Spring `RestClient` through the following dedicated
client classes:

  -----------------------------------------------------------------------
  Client                  Connected service       Responsibility
  ----------------------- ----------------------- -----------------------
  `AuthClient`            Auth Service            Retrieves customer
                                                  information

  `AccountClient`         Account Service         Retrieves accounts and
                                                  updates account status

  `TransactionClient`     Transaction Service     Retrieves transaction
                                                  information
  -----------------------------------------------------------------------

`RestClientConfig` provides the REST client configuration.

For successful integration, the dependent services must be running,
their configured URLs must be correct, and their APIs must be reachable.
Request and response formats must remain compatible between services.

## 15. Exception Handling

The application centralizes error handling using:

-   `GlobalExceptionHandler`
-   `RemoteServiceException`
-   `ErrorResponse`

This approach helps provide consistent error responses for invalid
input, application exceptions, and failures while communicating with
other microservices.

## 16. Important Configuration Notes

Before running the integrated application, verify:

1.  MySQL is running.
2.  The `admin_service_db` database exists.
3.  `DB_PASSWORD` is set correctly.
4.  `JWT_SECRET` matches the Auth Service's token-signing configuration.
5.  Auth Service is running and reachable.
6.  Account Service is running and reachable.
7.  Transaction Service is running and reachable.
8.  The service URLs in `application.properties` are correct.
9.  Port `8084` is available.

The Admin Service listens on port **8084** in the current configuration.

## 17. Troubleshooting

  -----------------------------------------------------------------------
  Problem                             Things to check
  ----------------------------------- -----------------------------------
  Application fails to start          Check Java version, Maven output,
                                      and application configuration

  Database connection fails           Check MySQL status, database name,
                                      username, password, and
                                      `DB_PASSWORD`

  JWT validation fails                Check token expiry, signing secret,
                                      and token claims

  HTTP 403 response                   Verify that the token belongs to a
                                      user with the ADMIN role

  HTTP 401 response                   Verify that a valid bearer token is
                                      supplied

  Remote service call fails           Check whether the dependent service
                                      is running and its URL is correct

  Port 8084 is already in use         Stop the process using the port or
                                      configure a different port
  -----------------------------------------------------------------------

## 18. Future Enhancements

Potential improvements include:

-   Pagination and sorting for customer and account lists.
-   More advanced transaction search and filtering.
-   Dashboard charts and banking analytics.
-   More detailed audit records, including administrator identity and
    timestamps.
-   Exporting reports to CSV or PDF.
-   Expanded unit and integration test coverage.
-   API documentation using Swagger/OpenAPI.
-   Containerized deployment using Docker.
-   Centralized monitoring and structured application logging.

## 19. Conclusion

The Banking Management System -- Admin Service provides a centralized
administrative layer for monitoring and managing banking operations. Its
modular design separates controllers, business logic, data-transfer
objects, security, persistence, and inter-service communication.

Using Java, Spring Boot, Spring Security, JWT, MySQL, Thymeleaf, and
REST-based integration, the service supports secure administrative
operations and integration with the other banking microservices.

------------------------------------------------------------------------

**Project:** Banking Management System -- Admin Service\
**Module:** Admin Service\
**Language:** Java 17\
**Framework:** Spring Boot\
**Database:** MySQL\
**Application Port:** 8084\
**Architecture:** Microservices\
**Repository:** https://github.com/ujjvalap/Banking_Management
