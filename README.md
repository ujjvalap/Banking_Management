# Account Service Module

Part of the **Banking Management System**\
GitHub branch: `feature/account-service`

## 1. Module Overview

The Account Service module provides the backend functionality for
managing bank customers and their accounts. It exposes REST APIs that
allow clients such as Postman or a frontend application to create
customers and accounts, view account information, deposit and withdraw
money, transfer funds, and review transaction and account-status
history.

The application is built with Java and Spring Boot. It uses Spring Data
JPA and Hibernate to communicate with a MySQL database.

## 2. Main Features

-   **Customer management:** register customers, view one or all
    customers, update customer details, and delete customers.
-   **Account management:** create accounts, retrieve account details,
    and list accounts belonging to a customer.
-   **Balance operations:** deposit and withdraw money, with
    account-status and amount checks.
-   **Money transfers:** transfer money between two accounts and record
    the debit and credit entries.
-   **Transaction history:** retrieve transactions for an account.
-   **Account status management:** change an account's status and view
    its status-change history.
-   **Validation and error handling:** validate request data and return
    structured error responses.
-   **Security foundation:** includes JWT-related utility/filter classes
    and Spring Security configuration. Check the current security
    configuration before exposing the service publicly.

## 3. Technology Stack

  Technology              Purpose
  ----------------------- ------------------------------------------
  Java                    Main programming language
  Spring Boot             Application framework and REST API setup
  Spring Web              HTTP controllers and REST endpoints
  Spring Data JPA         Repository and database access
  Hibernate               ORM implementation
  MySQL                   Relational database
  Maven                   Dependency and build management
  Spring Security         API security configuration
  JWT                     Token utility and request filtering
  Bean Validation         Request-field validation
  Thymeleaf               Server-side HTML templates
  HTML, CSS, JavaScript   Basic web page resources
  Postman                 API testing

## 4. Project File Structure

The following is the main application structure. The Maven wrapper and
project configuration files are also shown.

``` text
account-service/
│
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
│
├── mvnw
├── mvnw.cmd
├── pom.xml
├── .gitignore
├── .gitattributes
├── README.md
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/sprint/bank/account/
    │   │       │
    │   │       ├── AccountServiceApplication.java
    │   │       │
    │   │       ├── controller/
    │   │       │   ├── AccountController.java
    │   │       │   ├── AccountStatusHistoryController.java
    │   │       │   ├── CustomerController.java
    │   │       │   └── TransactionController.java
    │   │       │
    │   │       ├── service/
    │   │       │   ├── AccountService.java
    │   │       │   ├── AccountStatusHistoryService.java
    │   │       │   ├── CustomerService.java
    │   │       │   ├── TransactionService.java
    │   │       │   └── impl/
    │   │       │       ├── AccountServiceImpl.java
    │   │       │       ├── AccountStatusHistoryServiceImpl.java
    │   │       │       ├── CustomerServiceImpl.java
    │   │       │       └── TransactionServiceImpl.java
    │   │       │
    │   │       ├── repository/
    │   │       │   ├── AccountRepository.java
    │   │       │   ├── AccountStatusHistoryRepository.java
    │   │       │   ├── CustomerRepository.java
    │   │       │   └── TransactionRepository.java
    │   │       │
    │   │       ├── entity/
    │   │       │   ├── Account.java
    │   │       │   ├── AccountStatus.java
    │   │       │   ├── AccountStatusHistory.java
    │   │       │   ├── AccountType.java
    │   │       │   ├── Customer.java
    │   │       │   ├── Transaction.java
    │   │       │   └── TransactionType.java
    │   │       │
    │   │       ├── dto/
    │   │       │   ├── AccountRequest.java
    │   │       │   ├── AccountResponse.java
    │   │       │   ├── AccountStatusHistoryResponse.java
    │   │       │   ├── BalanceUpdateRequest.java
    │   │       │   ├── CustomerRequest.java
    │   │       │   ├── CustomerResponse.java
    │   │       │   ├── ErrorResponse.java
    │   │       │   ├── StatusUpdateRequest.java
    │   │       │   ├── TransactionResponse.java
    │   │       │   └── TransferRequest.java
    │   │       │
    │   │       ├── security/
    │   │       │   ├── JwtFilter.java
    │   │       │   ├── JwtUtil.java
    │   │       │   └── SecurityConfig.java
    │   │       │
    │   │       └── exception/
    │   │           ├── AccountFrozenException.java
    │   │           ├── AccountNotFoundException.java
    │   │           └── GlobalExceptionHandler.java
    │   │
    │   └── resources/
    │       ├── application.properties
    │       ├── data.sql
    │       ├── templates/
    │       │   ├── dashboard.html
    │       │   ├── accounts.html
    │       │   └── account-details.html
    │       └── static/
    │           ├── css/
    │           │   └── style.css
    │           └── js/
    │               └── account.js
    │
    └── test/
        └── java/
            └── com/sprint/bank/account_service/
                └── AccountServiceApplicationTests.java
```

## 5. Explanation of Packages and Important Files

### `AccountServiceApplication.java`

This is the main class and starting point of the Spring Boot
application. It contains the `main()` method and
`@SpringBootApplication` annotation. Run this class to start the
application.

### `controller/` --- Handles API requests

Controllers receive HTTP requests, call the appropriate service methods,
and return HTTP responses.

  ---------------------------------------------------------------------------
  File                                    Responsibility
  --------------------------------------- -----------------------------------
  `AccountController.java`                Account creation, account lookup,
                                          deposit, withdrawal, and status
                                          update endpoints

  `CustomerController.java`               Customer registration, lookup,
                                          listing, update, and deletion
                                          endpoints

  `TransactionController.java`            Money transfer and
                                          transaction-history endpoints

  `AccountStatusHistoryController.java`   Returns the status-change history
                                          for an account
  ---------------------------------------------------------------------------

### `service/` --- Contains business logic

The service layer contains the rules and operations of the application.
Keeping business logic here prevents controllers from becoming
overloaded.

The service interfaces define the operations available to the
application:

-   `AccountService.java` --- account and balance operations.
-   `CustomerService.java` --- customer operations.
-   `TransactionService.java` --- transfers and transaction history.
-   `AccountStatusHistoryService.java` --- account status-history
    retrieval.

### `service/impl/` --- Implements business logic

These classes provide the actual implementation of the service
interfaces:

-   `AccountServiceImpl.java`
-   `CustomerServiceImpl.java`
-   `TransactionServiceImpl.java`
-   `AccountStatusHistoryServiceImpl.java`

For example, `TransactionServiceImpl` performs transfer validations,
updates both account balances, and saves transaction records. The
transfer operation uses a transaction boundary so the related database
changes are handled together.

### `repository/` --- Communicates with the database

Repository interfaces use Spring Data JPA to perform database
operations. They provide methods to save, find, update, and delete
records without requiring the application to write every SQL statement
manually.

-   `AccountRepository.java` --- account data access.
-   `CustomerRepository.java` --- customer data access.
-   `TransactionRepository.java` --- transaction data access.
-   `AccountStatusHistoryRepository.java` --- status-history data
    access.

### `entity/` --- Represents database tables

Entity classes map Java objects to database tables.

  -----------------------------------------------------------------------
  File                                Purpose
  ----------------------------------- -----------------------------------
  `Account.java`                      Stores account number, type,
                                      balance, status, customer ID, and
                                      creation time

  `Customer.java`                     Stores customer details such as
                                      name, email, phone number, and
                                      address

  `Transaction.java`                  Stores deposit, withdrawal, and
                                      transfer records

  `AccountStatusHistory.java`         Stores previous status, new status,
                                      change time, and remarks

  `AccountType.java`                  Defines account types such as
                                      `SAVINGS` and `CURRENT`

  `AccountStatus.java`                Defines `ACTIVE`, `FROZEN`, and
                                      `CLOSED`

  `TransactionType.java`              Defines `DEPOSIT`, `WITHDRAWAL`,
                                      and `TRANSFER`
  -----------------------------------------------------------------------

### `dto/` --- Transfers data between API and application

DTO means **Data Transfer Object**. DTOs define the data accepted from
API requests or returned in API responses. They help avoid exposing
database entities directly.

-   `AccountRequest.java` --- data required to create an account.
-   `AccountResponse.java` --- account details returned to the client.
-   `BalanceUpdateRequest.java` --- amount supplied for a balance
    operation.
-   `StatusUpdateRequest.java` --- requested account-status change and
    remarks.
-   `CustomerRequest.java` and `CustomerResponse.java` --- customer
    input and output data.
-   `TransferRequest.java` --- sender account, receiver account, and
    transfer amount.
-   `TransactionResponse.java` --- transaction details returned to the
    client.
-   `AccountStatusHistoryResponse.java` --- account status-history
    details.
-   `ErrorResponse.java` --- structured error information.

### `security/` --- Security-related components

-   `SecurityConfig.java` --- configures Spring Security rules.
-   `JwtUtil.java` --- contains JWT token-related utility operations.
-   `JwtFilter.java` --- processes JWT information in incoming requests.

**Note:** The presence of JWT classes does not by itself mean that every
endpoint is fully protected. Review the security rules and
authentication flow before deploying the application.

### `exception/` --- Handles application errors

-   `AccountFrozenException.java` --- represents an operation attempted
    on a frozen account.
-   `AccountNotFoundException.java` --- represents an account lookup
    failure.
-   `GlobalExceptionHandler.java` --- centralizes
    exception-to-HTTP-response handling.

### `src/main/resources/` --- Configuration and frontend resources

  -----------------------------------------------------------------------
  File or folder                      Purpose
  ----------------------------------- -----------------------------------
  `application.properties`            Spring Boot, MySQL, JPA, JWT, and
                                      server configuration

  `data.sql`                          SQL statements for initial/sample
                                      data

  `templates/`                        Thymeleaf HTML pages

  `static/css/style.css`              Styles for the web pages

  `static/js/account.js`              Browser-side JavaScript
  -----------------------------------------------------------------------

### `src/test/` --- Test source code

Contains the Spring Boot application test class. Add unit and
integration tests here as the module grows.

### Root-level project files

-   `pom.xml` --- Maven project definition, dependencies, and build
    configuration.
-   `mvnw` and `mvnw.cmd` --- Maven Wrapper scripts for Linux/macOS and
    Windows.
-   `.mvn/wrapper/maven-wrapper.properties` --- Maven Wrapper
    configuration.
-   `.gitignore` --- lists files and folders Git should not track.
-   `.gitattributes` --- Git file-handling and line-ending settings.
-   `README.md` --- project documentation.

## 6. REST API Endpoints

The application runs locally on port `8081`.

Base URL: `http://localhost:8081`

### Account APIs

  --------------------------------------------------------------------------------------------
  HTTP Method             Endpoint                                     Description
  ----------------------- -------------------------------------------- -----------------------
  `POST`                  `/api/accounts`                              Create a bank account

  `GET`                   `/api/accounts/{accountId}`                  Get account details by
                                                                       ID

  `GET`                   `/api/accounts/customer/{customerId}`        Get accounts for a
                                                                       customer

  `POST`                  `/api/accounts/{accountId}/deposit`          Deposit money

  `POST`                  `/api/accounts/{accountId}/withdraw`         Withdraw money

  `PUT`                   `/api/accounts/{accountId}/status`           Change account status

  `GET`                   `/api/accounts/{accountId}/status-history`   View account status
                                                                       history
  --------------------------------------------------------------------------------------------

### Customer APIs

  HTTP Method   Endpoint                        Description
  ------------- ------------------------------- -------------------------
  `POST`        `/api/customers`                Register a customer
  `GET`         `/api/customers/{customerId}`   Get customer details
  `GET`         `/api/customers`                Get all customers
  `PUT`         `/api/customers/{customerId}`   Update customer details
  `DELETE`      `/api/customers/{customerId}`   Delete a customer

### Transaction APIs

  -----------------------------------------------------------------------------------------
  HTTP Method             Endpoint                                  Description
  ----------------------- ----------------------------------------- -----------------------
  `GET`                   `/api/transactions/account/{accountId}`   Get transaction history

  `POST`                  `/api/transactions/transfer`              Transfer money between
                                                                    accounts
  -----------------------------------------------------------------------------------------

Example transfer request:

``` json
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500.00
}
```

Use Postman to send requests and inspect responses. Request fields and
validation requirements are defined by the DTO classes.

## 7. Database Setup

1.  Install and start MySQL.
2.  Create the database:

``` sql
CREATE DATABASE banking_db;
```

3.  Configure the database credentials using environment variables.
4.  Start the Spring Boot application. Hibernate is configured to update
    the schema in the development setup.

The current `application.properties` expects these environment
variables:

  Variable        Description
  --------------- ------------------------------------------------
  `DB_USERNAME`   MySQL username (defaults to `root` if not set)
  `DB_PASSWORD`   MySQL password
  `JWT_SECRET`    Secret used for JWT signing

Do not commit real database passwords or JWT secrets to GitHub. Keep
local credentials in your IDE run configuration or another untracked
local configuration.

## 8. Run the Application

1.  Clone the repository:

``` bash
git clone https://github.com/ujjvalap/Banking_Management.git
```

2.  Change to the repository directory:

``` bash
cd Banking_Management
```

3.  Check out the account-service branch:

``` bash
git checkout feature/account-service
```

4.  Open the project in IntelliJ IDEA and load it as a Maven project.
5.  Create the `banking_db` database in MySQL.
6.  Set `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` in the IntelliJ
    Run Configuration environment variables.
7.  Run `AccountServiceApplication.java`.

The application should start on:

``` text
http://localhost:8081
```

## 9. API Testing

Use Postman with this base URL:

``` text
http://localhost:8081
```

Test the customer, account, deposit, withdrawal, transfer,
transaction-history, and status-history endpoints. Use valid IDs and
request bodies that match the DTO definitions.

## 10. Git Branch

This module is maintained on:

``` text
feature/account-service
```

The branch is pushed to the team repository:

https://github.com/ujjvalap/Banking_Management

Changes can be reviewed through a pull request before merging into
`main`.

------------------------------------------------------------------------

**Account Service Module --- Banking Management System**
