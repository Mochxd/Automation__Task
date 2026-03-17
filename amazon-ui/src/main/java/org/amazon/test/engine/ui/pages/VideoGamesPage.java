package org.amazon.test.engine.ui.pages;

import io.qameta.allure.Step;
import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.ui.driver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.TimeoutException;

public class VideoGamesPage {

    private final Driver driver;

    /* ======== Locators ======== */
    private static final By ALL_VIDEO_GAMES_MENU = By.xpath("//*[@id='hmenu-content']//a[text()='All Video Games']");
    private static final By ALL_VIDEO_GAMES_LINK = By.linkText("All Video Games");
    private static final By SEARCH_RESULTS       = By.cssSelector("div[data-component-type='s-search-result']");

    public VideoGamesPage(Driver driver) {
        this.driver = driver;
    }

    @Step("Choose All Video Games)")
    public ProductListPage clickAllVideoGames() {
        try {
            driver.element()
                  .scrollIntoView(ALL_VIDEO_GAMES_MENU)
                  .clickByJS(ALL_VIDEO_GAMES_MENU);
        } catch (TimeoutException | org.openqa.selenium.NoSuchElementException
                 | ElementClickInterceptedException e) {
            try {
                driver.element().clickByJS(ALL_VIDEO_GAMES_LINK);
            } catch (TimeoutException | org.openqa.selenium.NoSuchElementException e2) {
                if (driver.element().findElements(SEARCH_RESULTS).isEmpty()) throw e2;
            }
        }
        logsUtils.info("Navigated to All Video Games listing");
        return new ProductListPage(driver);
    }
}
