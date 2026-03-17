package org.amazon.test.engine.ui.interactions;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public class BrowserActions {

    private final WebDriver driver;

    public BrowserActions(WebDriver driver) {
        this.driver = driver;
    }

    public BrowserActions navigateTo(String url) {
        driver.navigate().to(url);
        return this;
    }

    public BrowserActions getToURL(String url) {
        driver.get(url);
        return this;
    }

    public String getCurrentURL() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public BrowserActions navigateForward() {
        driver.navigate().forward();
        return this;
    }

    public BrowserActions navigateBack() {
        driver.navigate().back();
        return this;
    }

    public BrowserActions refresh() {
        driver.navigate().refresh();
        return this;
    }

    public BrowserActions addCookie(Cookie cookie) {
        driver.manage().addCookie(cookie);
        return this;
    }

    public BrowserActions deleteAllCookies() {
        driver.manage().deleteAllCookies();
        return this;
    }

    public BrowserActions maximize() {
        driver.manage().window().maximize();
        return this;
    }

    public BrowserActions scrollToBottom() {
        new Actions(driver).scrollByAmount(0, 3000).build().perform();
        return this;
    }
}
