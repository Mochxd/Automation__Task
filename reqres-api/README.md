# ReqRes API Tests

API automation for [reqres.in](https://reqres.in): create user, get user, update user, and error-handling scenarios. Built with **RestAssured**, **TestNG**, and **Maven**. Optional **WireMock** for offline runs and **Allure** for reports.

---

## Prerequisites

- **Java 11+**
- **Maven 3.6+**

---

## Tech Stack

| Component        | Technology        |
|-----------------|-------------------|
| API client      | RestAssured 5.x   |
| Test runner     | TestNG            |
| Build / deps     | Maven             |
| Mock server     | WireMock (optional) |
| Reporting       | Allure + TestNG listener |
| Logging         | Log4j             |

---

## Project Structure

```
reqres-api/
├── pom.xml
├── tests.xml                 # TestNG suite
├── reportGeneration.bat      # Open Allure report (target/allure-results)
├── src/
│   ├── main/
│   │   ├── java/org/reqres/test/engine/
│   │   │   ├── api/          # Client, constants, responses, endpoints, verification
│   │   │   ├── utilities/    # Properties, logs, Allure reporting
│   │   │   └── listeners/    # TestNG listener (lifecycle + report config)
│   │   └── resources/
│   │       ├── config.properties
│   │       ├── Reporting.properties
│   │       └── testData/     # UserRequest.json, UpdatedUserRequest.json, InvalidUserRequest.json
│   └── test/
│       └── java/
│           ├── apisTestCases/ # CreateUserTest, GetUserTest, UpdateUserTest, ApiErrorHandlingTest
│           ├── base/         # BaseTest (config, WireMock start/stop)
│           └── helpers/      # UserIdHolder (share created user id between tests)
```

---

## Configuration

### config.properties (`src/main/resources/config.properties`)

| Key                 | Description                    | Example           |
|---------------------|--------------------------------|-------------------|
| baseUrl             | API base URL                   | https://reqres.in |
| createUserEndPoint  | Path for POST create user      | /api/users        |
| useWireMock         | Use WireMock instead of live   | true / false      |
| wireMockPort        | WireMock port when enabled     | 8089              |

### Reporting.properties (`src/main/resources/Reporting.properties`)

| Key                         | Description                          |
|-----------------------------|--------------------------------------|
| CleanAllureReport           | Clean allure-results before run      |
| OpenAllureReportAfterExecution | Open report after tests (via bat)  |

### Environment / system overrides

- **BASE_URL** – overrides `baseUrl`
- **WIREMOCK_PORT** – overrides `wireMockPort`
- **USE_WIREMOCK** – overrides `useWireMock` (e.g. `true` / `false`)

Same keys can be set as JVM system properties: `baseUrl`, `wireMockPort`, `useWireMock`.

---

## Running Tests

| Command              | Mode   | Description |
|----------------------|--------|-------------|
| `mvn test`           | WireMock | Default: uses local WireMock stub. |
| `mvn test -Plive`    | Live   | Uses real reqres.in (useWireMock=false). |
| `mvn test -DsuiteXmlFile=tests.xml` | — | Run the TestNG suite in `tests.xml`. |

After a run, Allure results are in `target/allure-results`. To view the report:

- **Windows:** `reportGeneration.bat` (runs `allure serve target/allure-results`)
- **Manual:** `allure serve target/allure-results`

---

## Test Scenarios

| Scenario        | Description |
|-----------------|-------------|
| **Create user** | POST `/api/users` with name, job, age; assert 201, id/name/job; store id for later tests. |
| **Get user**    | GET `/api/users/{id}` using id from create; assert 200 and data matches created user. |
| **Update user** | PUT `/api/users/{id}` with updated name/job; assert 200 and updated fields. |
| **Error handling** | 404 for non-existent user, empty/invalid body POST (no 500), DELETE 204; assertions and logging. |

Tests run in order (Create → Get → Update) and share the created user id via `UserIdHolder`.

---

## Reporting

- **Allure:** Results written to `target/allure-results`; environment and executor info set by `AllureReportHelper`.
- **TestNG listener:** `TestNGListener` handles execution/test lifecycle and report options (clean results, open report after run when configured).
- **Logs:** Log4j via `logsUtils`; `ApiVerification` logs status, body, and field mismatches on failure.

---

## Adding or Changing Tests

- **New test class:** Place in `src/test/java/apisTestCases/` and add the class to `tests.xml` if you use the suite.
- **Test data:** Edit or add JSON under `src/main/resources/testData/` and load in tests (e.g. same pattern as `UserRequest.json`).
- **Config:** Change `config.properties` or use env/system properties; no recompile needed for URL or WireMock settings.
