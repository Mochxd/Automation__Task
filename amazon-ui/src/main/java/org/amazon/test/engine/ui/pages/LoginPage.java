package org.amazon.test.engine.ui.pages;

import io.qameta.allure.Step;
import org.amazon.test.engine.common.logs.logsUtils;
import org.amazon.test.engine.ui.driver.Driver;
import org.openqa.selenium.By;

public class LoginPage {

    private final Driver driver;

    /* ======== Locators ======== */
    private static final By SIGN_IN_LINK      = By.id("nav-link-accountList");
    private static final By EMAIL_FIELD   = By.cssSelector("input[name='email']");
    private static final By CONTINUE_BTN      = By.id("continue");
    private static final By PASSWORD_FIELD    = By.id("ap_password");
    private static final By SIGN_IN_BTN       = By.id("signInSubmit");

    public LoginPage(Driver driver) {
        this.driver = driver;
    }

    @Step("Open amazon.eg and login")
    public AllMenuPage login(String email, String password) {
        driver.element().click(SIGN_IN_LINK);
        driver.element().fillField(EMAIL_FIELD, email);
        driver.element()
              .click(CONTINUE_BTN)
              .fillField(PASSWORD_FIELD, password)
              .click(SIGN_IN_BTN);

        logsUtils.info("Logged in as: ", email);
        return new AllMenuPage(driver);
    }
}
