## Amazon Egypt UI Task

This project automates the end‑to‑end Amazon Egypt shopping flow described in the task:

1. Open `https://www.amazon.eg/` and log in.
2. Open the **All** menu from the left side.
3. Click **Video Games** then **All Video Games**.
4. From the left filter menu, apply **Free Shipping** and condition **New**.
5. From the sort menu on the right, choose **Price: High to Low**.
6. Add all products where the price is **below 15,000 EGP**. If no product on the current page matches, go to the next page.
7. Verify that **all** added products appear in the cart.
8. Add a delivery address and choose **Cash on Delivery**.
9. Verify that the **total amount = items + shipping fees** (before discounts, if any).

The implementation uses Java, Selenium WebDriver, TestNG, Allure, and a Page Object Model with shared `ElementActions`.

---

### 1. Project structure (high level)

- `src/test/java/ui/AmazonFlowTest.java`  
  Orchestrates the full UI flow (steps 1–9) in a single TestNG test.

- `src/main/java/org/amazon/test/engine/ui/pages/`  
  Page Objects:
  - `LoginPage` – login to Amazon.eg.
  - `AllMenuPage` – open the hamburger menu and navigate to **Video Games**.
  - `VideoGamesPage` – click **All Video Games**.
  - `ProductListPage` – apply filters/sort, add all products below 15k EGP (with pagination), open cart.
  - `CartPage` – verify that every added product is present in the cart and proceed to checkout.
  - `CheckoutPage` – always add a **new** delivery address, select **Cash on Delivery**, and verify order totals.

- `src/main/java/org/amazon/test/engine/ui/interactions/ElementActions.java`  
  Shared interaction helper (click, JS click, scrolling, waits for `findElements`, autocomplete helpers, etc.).

- `src/main/resources/`  
  - `TestData.properties` – test data such as email/password and address. **You must fill your own values locally (see below).**
  - `WebConfigurations.properties` – browser / environment settings.
  - `Reporting.properties` – Allure and reporting flags.
  - `log4j2.properties` – logging configuration with rolling file logs under `test-outputs/Logs`.

---

### 2. Prerequisites

- JDK 21 (or compatible with the configured Maven toolchain, if any).
- Maven 3.x installed and on the `PATH`.
- Google Chrome installed (the framework currently targets a Chrome driver).

---

### 3. Test data – **add your own credentials**

For security, real credentials are **not** committed.

Before running the tests, update `src/main/resources/TestData.properties` with your own data:

```properties
amazon.email=YOUR_EMAIL_HERE
amazon.password=YOUR_PASSWORD_HERE

checkout.fullName=Your Full Name
checkout.mobileNumber=01000000000
checkout.addressLine=Street name
checkout.building=Building / Apartment
checkout.city=Cai
checkout.district=15th of May City-15 May City
checkout.governorate=Cairo
```

Notes:
- Use a test / disposable Amazon.eg account where possible.
- The address fields are aligned with the current Amazon Egypt checkout UI (city, district, governorate with autocomplete).

---

### 4. Design overview

- **Page Object Model**: Each screen in the flow has its own page class. Pages only expose business‑level actions (`login`, `applyFreeShippingFilter`, `addDeliveryAddress`, etc.) and keep raw Selenium calls inside `ElementActions`. This keeps the test (`AmazonFlowTest`) readable and close to the original task wording.
- **ElementActions as a facade**: All low‑level WebDriver and JavaScript operations go through `ElementActions` (click, JS click, scroll, waits, autocomplete helpers). This makes it easy to change interaction strategy in one place if Amazon’s UI changes.
- **Config‑driven waits**: A custom `WebDriverListener` adds waits for visibility/clickability on `findElement` and `click`. The timeout comes from `WaitTimeoutSeconds` in `WebConfigurations.properties` and defaults to **30 seconds** if not set. For collections, `ElementActions.findElements` uses the same configurable timeout for presence.
- **Resilient locators**: Locators prefer IDs and short CSS where possible, with carefully chosen XPath only when needed (e.g., menu entries). Names of locator fields (e.g., `FREE_SHIPPING_FILTER`, `CASH_ON_DELIVERY_RADIO`) match what a tester sees on the page.
- **Strong assertions**: `CartPage.verifyProductsInCart` requires all expected items to be present (fuzzy title match), and `CheckoutPage.verifyOrderSummary` checks that `items + shipping == total` within a small tolerance.
- **Logging and reporting**: Log4j2 writes colored console logs and rolling files under `test-outputs/Logs`, and Allure reports provide step‑level visibility with `@Step` annotations on all page actions.

---

### 5. How to run the tests

From the project root (`Automation_Task/amazon-ui`):

```bash
mvn test
```

What happens on a typical run:

- A Chrome browser is started and the full `fullAmazonShoppingFlow` test is executed.
- Logs are written to:
  - `test-outputs/Logs/logs.log` (current run)
  - Rotated archives: `test-outputs/Logs/logs-YYYY-MM-DD-i.log.gz`
- Allure results are generated under `target/allure-results`.
- At the end of the TestNG execution, the Allure report is built and opened automatically (controlled by `Reporting.properties`).

---

### 6. Notes about reliability and behavior

- The cart verification step is strict by design: the test fails if **any** expected product is missing in the cart.
- Product prices and availability on Amazon.eg are dynamic; occasionally Amazon may skip or alter items (e.g., due to stock or offers). When that happens, the framework logs which expected product is missing so it is easy to diagnose whether it is a real site behavior or a locator issue.
- The framework avoids `Thread.sleep` and relies on:
  - A WebDriver listener that waits for element visibility/clickability using the configurable timeout.
  - Custom `ElementActions.findElements` with an explicit wait for plural lookups (same timeout).

This makes the solution both readable and close to production‑grade, while staying aligned with the original interview task.

---

### 7. High‑level design diagram

```mermaid
flowchart TD
    subgraph Test Layer
        T[AmazonFlowTest<br/>fullAmazonShoppingFlow()]
    end

    subgraph Core Driver
        D[Driver<br/>wraps WebDriver + EventFiringDecorator]
        L[WebDriverListener<br/>config‑driven waits]
        EA[ElementActions<br/>UI interaction facade]
        BA[BrowserActions]
    end

    subgraph Pages
        LP[LoginPage]
        AMP[AllMenuPage]
        VGP[VideoGamesPage]
        PLP[ProductListPage]
        CP[CartPage]
        CHP[CheckoutPage]
    end

    subgraph Config & Infra
        WC[WebConfigurations.properties<br/>browser, base URL, WaitTimeoutSeconds]
        TD[TestData.properties<br/>credentials, address]
        PM[PropertiesManager]
        LG[log4j2.properties<br/>rolling logs]
        RP[Reporting.properties<br/>Allure flags]
    end

    T -->|uses| LP
    LP -->|returns| AMP
    AMP -->|returns| VGP
    VGP -->|returns| PLP
    PLP -->|returns| CP
    CP -->|returns| CHP

    LP --- D
    AMP --- D
    VGP --- D
    PLP --- D
    CP --- D
    CHP --- D

    D --> L
    D --> EA
    D --> BA

    EA -->|reads| WC
    L  -->|reads| WC
    PM --> WC
    PM --> RP
    PM --> TD

    D -. logs .-> LG
    T -. reports .-> RP
```

