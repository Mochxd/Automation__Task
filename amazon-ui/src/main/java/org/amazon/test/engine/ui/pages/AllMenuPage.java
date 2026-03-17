package org.amazon.test.engine.ui.pages;

import io.qameta.allure.Step;
import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.ui.driver.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

public class AllMenuPage {

    private final Driver driver;

    /* ======== Locators ======== */
    private static final By ALL_MENU_BTN            = By.id("nav-hamburger-menu");
    private static final By ALL_MENU_PANEL          = By.id("hmenu-content");
    private static final By SEE_ALL_CATEGORIES_BTN  = By.cssSelector("#hmenu-content .hmenu-compressed-btn");
    private static final By VIDEO_GAMES_MENU_LINK   = By.xpath("//*[@id='hmenu-content']//a[contains(.,'Video Games')]");

    public AllMenuPage(Driver driver) {
        this.driver = driver;
    }

    @Step("Open All menu from the left")
    public AllMenuPage openAllMenu() {
        driver.element().click(ALL_MENU_BTN);
        logsUtils.info("All menu opened");
        return this;
    }

    @Step("Click Video Games in side menu")
    public VideoGamesPage clickVideoGames() {
        driver.element().scrollElementToBottom(ALL_MENU_PANEL);

        try {
            driver.element().click(SEE_ALL_CATEGORIES_BTN);
        } catch (TimeoutException | org.openqa.selenium.NoSuchElementException e) {
            logsUtils.info("See All Categories not visible — skipping");
        }

        driver.element()
              .scrollIntoView(VIDEO_GAMES_MENU_LINK)
              .click(VIDEO_GAMES_MENU_LINK);

        logsUtils.info("Clicked Video Games");
        return new VideoGamesPage(driver);
    }
}
