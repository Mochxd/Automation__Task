package org.amazon.test.engine.ui.listeners.webdriver;

import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.common.properties.PropertiesManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.NoSuchElementException;

public class WebDriverListener implements org.openqa.selenium.support.events.WebDriverListener {

    private final WebDriver driver;
    private final int waitTimeoutSeconds;

    public WebDriverListener(WebDriver driver) {
        this.driver = driver;
        this.waitTimeoutSeconds = PropertiesManager.getIntConfig("WaitTimeoutSeconds", 30);
    }

    @Override
    public void afterGet(WebDriver driver, String url) {
        logsUtils.info("Navigated to: ", url);
    }

    @Override
    public void afterGetTitle(WebDriver driver, String result) {
        logsUtils.info("Page title: ", result);
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, String url) {
        logsUtils.info("Navigated to url: ", url);
    }

    @Override
    public void afterBack(WebDriver.Navigation navigation) {
        logsUtils.info("Navigated back");
    }

    @Override
    public void afterForward(WebDriver.Navigation navigation) {
        logsUtils.info("Navigated forward");
    }

    @Override
    public void afterRefresh(WebDriver.Navigation navigation) {
        logsUtils.info("Page refreshed");
    }

    @Override
    public void beforeDeleteAllCookies(WebDriver.Options options) {
        logsUtils.info("Deleting all cookies");
    }

    @Override
    public void beforeFindElement(WebDriver driver, By locator) {
        try {
            new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(waitTimeoutSeconds))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logsUtils.error("Element not visible after ", String.valueOf(waitTimeoutSeconds), "s: ", locator.toString());
            throw e;
        }
    }

    @Override
    public void beforeClick(WebElement element) {
        try {
            new WebDriverWait(this.driver, Duration.ofSeconds(waitTimeoutSeconds))
                    .until(ExpectedConditions.elementToBeClickable(element));
            logsUtils.info("Clicking: ", getElementName(element));
        } catch (TimeoutException e) {
            logsUtils.error("Element not clickable after ", String.valueOf(waitTimeoutSeconds), "s");
            throw e;
        }
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        logsUtils.info("Typing \"", String.join("", keysToSend), "\" into ", getElementName(element));
    }

    @Override
    public void beforeClear(WebElement element) {
        logsUtils.info("Clearing: ", getElementName(element));
    }

    @Override
    public void afterGetText(WebElement element, String result) {
        logsUtils.info("Got text \"", result, "\" from ", getElementName(element));
    }

    @Override
    public void afterClose(WebDriver driver) {
        logsUtils.info("Browser window closed");
    }

    @Override
    public void afterQuit(WebDriver driver) {
        logsUtils.info("WebDriver quit");
    }

    private String getElementName(WebElement element) {
        try {
            String name = element.getAccessibleName();
            return (name == null || name.isBlank()) ? "element" : "\"" + name + "\"";
        } catch (Exception e) {
            return "element";
        }
    }
}
