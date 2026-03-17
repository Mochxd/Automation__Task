package org.amazon.test.engine.ui.driver;

import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.common.properties.PropertiesManager;
import org.amazon.test.engine.ui.interactions.BrowserActions;
import org.amazon.test.engine.ui.interactions.ElementActions;
import org.amazon.test.engine.ui.listeners.webdriver.WebDriverListener;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;

public class Driver {

    private final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public Driver() {
        initDriver(PropertiesManager.getConfig("BrowserType"));
    }

    public Driver(String browserType) {
        initDriver(browserType);
    }

    private void initDriver(String browserType) {
        WebDriver rawDriver = getFactory(browserType).startDriver();
        driver.set(new EventFiringDecorator<>(WebDriver.class,
                new WebDriverListener(rawDriver)).decorate(rawDriver));

        logsUtils.info("Browser started: ", browserType);
        driver.get().manage().window().maximize();

        String baseUrl = PropertiesManager.getConfig("BaseURL");
        if (!baseUrl.isBlank()) {
            driver.get().navigate().to(baseUrl);
        }
    }

    private DriverAbstract getFactory(String browserType) {
        return switch (browserType.toUpperCase()) {
            case "CHROME"  -> new ChromeDriverFactory();
            case "FIREFOX" -> new FireFoxDriverFactory();
            case "EDGE"    -> new EdgeDriverFactory();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browserType);
        };
    }

    public WebDriver get() {
        return driver.get();
    }

    public void quit() {
        logsUtils.info("Quitting WebDriver");
        driver.get().quit();
        driver.remove();
    }

    public ElementActions element() {
        return new ElementActions(driver.get());
    }

    public BrowserActions browser() {
        return new BrowserActions(driver.get());
    }
}
