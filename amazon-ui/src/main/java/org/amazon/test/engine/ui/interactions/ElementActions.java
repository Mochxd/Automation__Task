package org.amazon.test.engine.ui.interactions;

import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.common.properties.PropertiesManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ElementActions {

    private static final int WAIT_TIMEOUT_SECONDS = PropertiesManager.getIntConfig("WaitTimeoutSeconds", 30);
    private final WebDriver driver;

    public ElementActions(WebDriver driver) {
        this.driver = driver;
    }

    // ── Core interactions ──────────────────────────────────────────────────────

    public ElementActions click(By locator) {
        logsUtils.info("Clicking element: ", locator.toString());
        driver.findElement(locator).click();
        return this;
    }

    public ElementActions fillField(By locator, String text) {
        clearField(locator);
        logsUtils.info("Filling field ", locator.toString(), " with value: ", text);
        driver.findElement(locator).sendKeys(text);
        return this;
    }

    public ElementActions clearField(By locator) {
        driver.findElement(locator).clear();
        return this;
    }

    public ElementActions selectByVisibilityOfText(By locator, String text) {
        new Select(driver.findElement(locator)).selectByVisibleText(text);
        return this;
    }

    public ElementActions selectByValue(By locator, String value) {
        new Select(driver.findElement(locator)).selectByValue(value);
        return this;
    }

    // ── JavaScript interactions ────────────────────────────────────────────────

    /** JS click by locator — bypasses overlays that intercept native click. */
    public ElementActions clickByJS(By locator) {
        js().executeScript("arguments[0].click();", driver.findElement(locator));
        return this;
    }

    /** JS click on a WebElement reference — used when element was found via findElements. */
    public ElementActions clickByJS(WebElement element) {
        js().executeScript("arguments[0].click();", element);
        return this;
    }

    /** Try primary locator first via JS click; fall back to secondary on failure. */
    public ElementActions clickByJSWithFallback(By primary, By fallback) {
        try {
            clickByJS(primary);
        } catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            clickByJS(fallback);
        }
        return this;
    }

    /** Click the first element matched by locator (no listener wait — uses findElements). */
    public ElementActions clickFirst(By locator) {
        List<WebElement> els = driver.findElements(locator);
        if (!els.isEmpty()) els.get(0).click();
        return this;
    }

    /** Hide DOM elements by CSS selector. Silently ignores selectors with no match. */
    public ElementActions dismissOverlay(String... cssSelectors) {
        StringBuilder sb = new StringBuilder();
        for (String sel : cssSelectors) {
            sb.append("var e=document.querySelector('").append(sel)
              .append("');if(e)e.style.display='none';");
        }
        js().executeScript(sb.toString());
        return this;
    }

    /** Execute arbitrary JavaScript and return the result. */
    public Object executeScript(String script, Object... args) {
        return js().executeScript(script, args);
    }

    // ── Element queries ────────────────────────────────────────────────────────

    public String getTextOf(By locator) {
        return driver.findElement(locator).getText();
    }

    public String getAttribute(By locator, String attribute) {
        return driver.findElement(locator).getAttribute(attribute);
    }

    public boolean isDisplayed(By locator) {
        return driver.findElement(locator).isDisplayed();
    }

    public boolean isEnabled(By locator) {
        return driver.findElement(locator).isEnabled();
    }

    /** True if the field is missing or its value is blank. */
    public boolean isFieldEmpty(By locator) {
        List<WebElement> els = driver.findElements(locator);
        if (els.isEmpty()) return true;
        String val = els.get(0).getAttribute("value");
        return val == null || val.isBlank();
    }

    /** True if at least one element matches the locator. */
    public boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /** Wait for at least one element, then return all matches (covers the plural findElements case). */
    public List<WebElement> findElements(By locator) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (TimeoutException ignored) { }
        return driver.findElements(locator);
    }

    // ── Scroll helpers ─────────────────────────────────────────────────────────

    public ElementActions scrollToTop() {
        js().executeScript("window.scrollTo(0, 0);");
        return this;
    }

    public ElementActions scrollToBottom() {
        js().executeScript("window.scrollTo(0, document.body.scrollHeight);");
        return this;
    }

    public ElementActions scrollBy(int x, int y) {
        js().executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
        return this;
    }

    public ElementActions scrollIntoView(By locator) {
        js().executeScript("arguments[0].scrollIntoView({block:'nearest',behavior:'instant'});",
                driver.findElement(locator));
        return this;
    }

    public ElementActions scrollElementToBottom(By containerLocator) {
        js().executeScript("var el=arguments[0];if(el)el.scrollTop=el.scrollHeight;",
                driver.findElement(containerLocator));
        return this;
    }

    public ElementActions scrollToElement(By locator) {
        new Actions(driver).scrollToElement(driver.findElement(locator)).build().perform();
        return this;
    }

    public ElementActions hoverAndClick(By locator) {
        new Actions(driver).moveToElement(driver.findElement(locator)).click().build().perform();
        return this;
    }

    // ── Internal ───────────────────────────────────────────────────────────────

    private JavascriptExecutor js() {
        return (JavascriptExecutor) driver;
    }
}
