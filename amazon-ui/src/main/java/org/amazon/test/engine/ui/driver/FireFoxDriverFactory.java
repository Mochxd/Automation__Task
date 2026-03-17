package org.amazon.test.engine.ui.driver;

import org.amazon.test.engine.common.properties.PropertiesManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FireFoxDriverFactory extends DriverAbstract {

    @Override
    public WebDriver startDriver() {
        FirefoxOptions options = new FirefoxOptions();
        if (PropertiesManager.getConfig("HeadlessMode").equalsIgnoreCase("true")) {
            options.addArguments("--headless");
        }
        driver = new FirefoxDriver(options);
        return driver;
    }
}
