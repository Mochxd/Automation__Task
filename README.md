## Automation Task – UI and API Frameworks

This repository (`automation_task`) contains two independent automation projects under a shared Maven parent:

- **`amazon-ui`**: Selenium/TestNG UI automation for the Amazon Egypt shopping flow.
- **`reqres-api`**: RestAssured/TestNG API automation for `https://reqres.in` (create/get/update user + error handling).

Both modules use **Allure** for reporting and **Log4j** for logging.

---

## 1. Structure

```text
automation_task/
├── pom.xml              # Parent POM (versions, dependency management)
├── amazon-ui/           # Amazon Egypt UI task
└── reqres-api/          # ReqRes API task
```

Each module has its own `pom.xml`, `tests.xml`, `reportGeneration.bat`, `src/main`, and `src/test` folders.

For full details of each module’s design (pages, helpers, configs, flows), see:

- `amazon-ui/README.md`
- `reqres-api/README.md`

---

## 2. Tech Stack (shared concepts)

- **Language**: Java 21
- **Build**: Maven multi‑module (shared parent POM)
- **Test framework**: TestNG
- **Reporting**: Allure + custom TestNG listeners
- **Logging**: Log4j2 with console + rolling file logs

Module‑specific tools:

- `amazon-ui`: Selenium WebDriver, custom `ElementActions`, WebDriver listeners, POM for Amazon Egypt.
- `reqres-api`: RestAssured, WireMock (optional, for offline/mocked API runs).

---

## 3. Configuration Overview

### Amazon UI (`amazon-ui/src/main/resources`)

- `WebConfigurations.properties` – browser, base URL (`https://www.amazon.eg/`), wait timeouts.
- `TestData.properties` – login credentials and checkout address (you must fill your own values).
- `Reporting.properties` – Allure/reporting flags.
- `log4j2.properties` – logging configuration and log destinations.

### ReqRes API (`reqres-api/src/main/resources`)

- `config.properties` – base URL (`https://reqres.in`), `/api/users` endpoint, WireMock toggles and port.
- `Reporting.properties` – Allure/reporting flags.
- `testData/*.json` – request payloads for create/update/invalid user scenarios.

---

## 4. How to Run the Tests

From the root (`automation_task`), run each module separately.

### 4.1 Run ReqRes API tests

```bash
cd reqres-api
mvn test
```

### 4.2 Run Amazon UI tests

```bash
cd amazon-ui
mvn test -pl amazon-ui
```

Before running, update `amazon-ui/src/main/resources/TestData.properties` with valid Amazon.eg credentials and address.

### 4.3 Run only ReqRes API tests

```bash
mvn test -pl reqres-api
```

By default, `reqres-api` uses WireMock stubs; use the `-Plive` profile inside the module to hit the real API (see `reqres-api/README.md`).

---

## 5. Reports

Each module writes Allure results to its own `target/allure-results` folder.

To open a report:

- **Amazon UI**
  ```bash
  cd amazon-ui
  allure serve target/allure-results
  ```
  Or run `amazon-ui/reportGeneration.bat` on Windows.

- **ReqRes API**
  ```bash
  cd reqres-api
  allure serve target/allure-results
  ```
  Or run `reqres-api/reportGeneration.bat` on Windows.

---

## 6. Task Coverage (high level)

- **Amazon UI**
  - Implements the full Amazon Egypt flow: login → Video Games → All Video Games → filters (Free Shipping, New) → sort by Price: High to Low → add all products under 15,000 EGP (with pagination) → verify cart contents → add delivery address → select Cash on Delivery → verify that `items + shipping = total` (before discounts).
  - Page Object Model with reusable `ElementActions`, config‑driven waits, and strong assertions on cart and totals.

- **ReqRes API**
  - Uses `https://reqres.in` for:
    - Create user (POST `/api/users` with JSON body including name, job, age).
    - Retrieve user (GET `/api/users/{id}`) and validate fields.
    - Update user (PUT `/api/users/{id}`) and validate updated values.
  - Additional error‑handling tests for 404, invalid/empty payloads, and delete behavior.
  - Config‑driven base URL and endpoints, with WireMock support for stable offline runs.

