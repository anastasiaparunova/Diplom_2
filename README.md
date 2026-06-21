# Stellar Burgers — API Tests

API automated tests for the **Stellar Burgers** training service
(`https://stellarburgers.nomoreparties.site`). They cover the user and order
endpoints: registration, login, profile updates, order creation, and order
listing — in both positive and negative scenarios, for authorized and
unauthorized users.

## Tech stack

- Java 11
- JUnit 4
- REST Assured (HTTP requests and assertions)
- Allure (reporting)
- JavaFaker (test data generation)
- Gson
- Maven

## Test coverage

| Test class | Scenarios |
|---|---|
| `UserCreationTests` | create a unique user; reject duplicate user; validate empty email / password / name and all fields at once |
| `UserLoginTests` | successful login with valid credentials; errors on empty/invalid email and password |
| `UserFieldsUpdateTests` | update email / name / both fields for an authorized user; reject update to an email already in use; reject updates for an unauthorized user |
| `OrderCreationTests` | create an order as an authorized user; errors without ingredients and with invalid ingredients; behavior for an unauthorized user |
| `OrderListTest` | retrieve empty and non-empty order lists for an authorized user; reject retrieval for an unauthorized user |

Total: 27 tests. Test data is cleaned up after each run (teardown in `@After`).

## Architecture

```
src/test/java/
├── apiactions/        # API action layer (reusable steps)
│   ├── ApiActions.java        # base checks (status code, fields)
│   ├── UserApiActions.java    # user endpoint requests
│   └── OrderApiActions.java   # order endpoint requests
├── apitests/          # test classes
└── entities/          # data models (User, Order, TokenResponse)
```

Action steps are annotated with `@Step` and tests with `@DisplayName`, so the
Allure report reads as a human-friendly list of checks.

## Running the tests

```bash
mvn clean test
```

## Allure report

Generate and open the report after a run:

```bash
mvn allure:serve
```

Or build a static report:

```bash
mvn allure:report
# report: target/site/allure-maven-plugin/index.html
```

## Requirements

- JDK 11+
- Maven 3.6+
- [Allure CLI](https://docs.qameta.io/allure/) to view the report (for `allure:serve`)
