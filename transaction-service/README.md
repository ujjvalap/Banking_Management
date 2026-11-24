# Transaction Service

Transaction Service is a Spring Boot microservice for managing bank deposits, withdrawals, account transfers, and transaction statements. It runs on port `8082`, records transaction history in a database, and communicates with an Account Service running on port `8081`.

## Technology Stack

- Java 17
- Spring Boot 3.2.5
- Spring Web
- Spring Data JPA
- Spring Security
- JWT authentication
- H2 database (default)
- MySQL
- RestTemplate
- SpringDoc OpenAPI / Swagger
- Maven
- Lombok

## Module Structure

```text
src/main/java/com/sprint/bank/transaction
├── TransactionServiceApplication.java
├── client
│   └── AccountClient.java
├── config
│   ├── OpenApiConfig.java
│   └── RestClientConfig.java
├── controller
│   └── TransactionController.java
├── dto
│   ├── AccountDto.java
│   ├── DepositRequest.java
│   ├── ErrorResponse.java
│   ├── TransactionResponse.java
│   ├── TransferRequest.java
│   └── WithdrawRequest.java
├── entity
│   ├── Transaction.java
│   ├── TransactionType.java
│   └── Transfer.java
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── InsufficientBalanceException.java
│   └── TransactionFailedException.java
├── repository
│   ├── TransactionRepository.java
│   └── TransferRepository.java
├── security
│   ├── JwtFilter.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
└── service
    ├── TransactionService.java
    └── impl
        └── TransactionServiceImpl.java
```

## Application Flow

```text
Client
  |
  | Authorization: Bearer <JWT>
  v
JwtFilter
  |
  | Validates token and creates authenticated security context
  v
TransactionController
  |
  | Validates request and applies CUSTOMER/ADMIN role authorization
  v
TransactionServiceImpl
  |
  +-- AccountClient --> Account Service REST API
  |
  +-- TransactionRepository --> transactions table
  |
  +-- TransferRepository --> transfers table
  v
TransactionResponse or error response
```

The controller receives HTTP requests. Spring Validation checks the request body, and Spring Security verifies the JWT and role. The service implementation performs balance checks, calls the Account Service, and saves transaction records. The global exception handler converts business and validation errors into JSON responses.

## API Endpoints

All transaction endpoints require a JWT and either the `CUSTOMER` or `ADMIN` role.

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/accounts/{id}/deposit` | Deposit money into an account |
| `POST` | `/api/accounts/{id}/withdraw` | Withdraw money from an account |
| `POST` | `/api/transfers` | Transfer money between two accounts |
| `GET` | `/api/accounts/{id}/transactions` | Get account transaction history |

### Deposit

Request:

```http
POST /api/accounts/1/deposit
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "amount": 500.00
}
```

Flow:

1. Validate the positive, non-null amount.
2. Call `POST /api/accounts/1/deposit` on the Account Service.
3. Save a `DEPOSIT` transaction for account `1`.
4. Return the transaction details.

### Withdraw

Request:

```http
POST /api/accounts/1/withdraw
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "amount": 200.00
}
```

Flow:

1. Validate the positive, non-null amount.
2. Retrieve account details from the Account Service.
3. Check whether the account balance is sufficient.
4. Call the Account Service withdrawal operation.
5. Save a `WITHDRAW` transaction.
6. Return the transaction details.

### Transfer

Request:

```http
POST /api/transfers
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "sourceAccountId": 1,
  "destinationAccountId": 2,
  "amount": 150.00
}
```

Flow:

1. Validate account IDs and a positive amount.
2. Reject a transfer when source and destination are the same.
3. Retrieve the source account and verify its balance.
4. Withdraw the amount from the source account.
5. Deposit the amount into the destination account.
6. If destination deposit fails, reverse the source withdrawal.
7. Save a transfer record.
8. Save source and destination transaction records.

### Account Statement

Request:

```http
GET /api/accounts/1/transactions
Authorization: Bearer <JWT>
```

Transactions are returned in descending timestamp order.

## Code Explanation

### Application Entry Point

`TransactionServiceApplication` enables Spring Boot auto-configuration and component scanning.

```java
@SpringBootApplication
public class TransactionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }
}
```

### Controller

`TransactionController` exposes REST endpoints and delegates business operations to `TransactionService`. `@Valid` activates request validation. `@PreAuthorize` allows only `CUSTOMER` or `ADMIN` roles.

```java
@PostMapping("/accounts/{id}/deposit")
@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
public TransactionResponse deposit(@PathVariable Long id,
                                   @Valid @RequestBody DepositRequest request) {
    return transactionService.deposit(id, request);
}
```

### Request DTOs

Request records use Jakarta Validation annotations:

- `@NotNull` requires the field.
- `@Positive` requires an amount greater than zero.

Examples include `DepositRequest`, `WithdrawRequest`, and `TransferRequest`.

### Service Implementation

`TransactionServiceImpl` contains the business logic:

- Verifies source account balances.
- Calls the Account Service with `RestTemplate`.
- Rolls back a source withdrawal if the destination deposit fails.
- Saves transfer and transaction records.
- Converts entities to `TransactionResponse` DTOs.

All public write operations run inside Spring `@Transactional` boundaries.

### Account Service Integration

`AccountClient` performs HTTP calls to the Account Service.

```java
public AccountDto getAccount(Long id) {
    return restTemplate.getForObject(
            account.service.url + "/api/accounts/" + id,
            AccountDto.class);
}
```

The base URL is configured with:

```properties
account.service.url=http://localhost:8081
```

### Persistence

`Transaction` stores one account transaction:

- Account ID
- Transaction type
- Amount
- Timestamp
- Description

`Transfer` stores the source account, destination account, amount, and transfer timestamp.

`TransactionType` supports deposit, withdrawal, and transfer values.

Repositories extend `JpaRepository`:

```java
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId);
}
```

### JWT Security

`SecurityConfig` creates a stateless security configuration and registers `JwtFilter`.

```java
.sessionManagement(session ->
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```

`JwtFilter`:

1. Reads the `Authorization` header.
2. Checks for the `Bearer ` prefix.
3. Parses and validates the JWT.
4. Reads the `role` claim.
5. Creates a `ROLE_<role>` Spring Security authority.
6. Adds the authenticated user to the security context.

CSRF protection is disabled because the service uses stateless JWT authentication. Swagger and the H2 console are public.

### Error Handling

`GlobalExceptionHandler` handles:

- `InsufficientBalanceException`
- `TransactionFailedException`
- `MethodArgumentNotValidException`

Business and validation failures return HTTP `400 Bad Request` with:

```json
{
  "timestamp": "2026-11-24T20:22:52",
  "status": 400,
  "message": "Insufficient balance"
}
```

## Configuration

Default configuration in `src/main/resources/application.properties`:

```properties
spring.application.name=transaction-service
server.port=8082

spring.datasource.url=jdbc:h2:mem:transactiondb
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update

account.service.url=http://localhost:8081

jwt.secret=change-this-transaction-service-secret-key-for-jwt
jwt.expiration=86400000
```

In production, replace the JWT secret and configure a persistent database.

### H2 Console

Open:

```text
http://localhost:8082/h2-console
```

Use:

- JDBC URL: `jdbc:h2:mem:transactiondb`
- Username: `sa`
- Password: empty

### Swagger

Open:

```text
http://localhost:8082/swagger-ui.html
```

Use Swagger's `Authorize` option and enter:

```text
Bearer <JWT>
```

## Running the Module

### Prerequisites

- JDK 17
- Maven
- Account Service running at `http://localhost:8081`

### Start the Account Service

Start the bank Account Service first.

### Start Transaction Service

From the project directory:

```powershell
mvn clean spring-boot:run
```

Or build and run the JAR:

```powershell
mvn clean package
java -jar target/transaction-service-0.0.1-SNAPSHOT.jar
```

The service will be available at:

```text
http://localhost:8082
```

## Sample JWT Header

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.example-token
```

The JWT must use the same `jwt.secret` configured in both services and contain a role claim such as:

```json
{
  "sub": "customer@example.com",
  "role": "CUSTOMER"
}
```

## Transaction and Transfer Types

```java
public enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER
}
```

For a transfer, the module creates:

- One transfer record in `transfers`.
- One `TRANSFER` transaction for the source account.
- One `TRANSFER` transaction for the destination account.
